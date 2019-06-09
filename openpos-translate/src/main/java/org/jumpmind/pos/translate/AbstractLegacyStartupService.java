package org.jumpmind.pos.translate;

import java.io.File;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.util.NodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

public abstract class AbstractLegacyStartupService implements ILegacyStartupService {
    private final static Logger logger = LoggerFactory.getLogger(AbstractLegacyStartupService.class);

    @Value("${openpos.translate.external.process.enabled}")
    private boolean externalProcessEnabled;

    @Value("${openpos.translate.orpos.headless.working.dir}")
    private String appWorkingDir;

    @Value("${openpos.translate.rmi.registry.port:9598}")
    private int rmiRegistryPort;

    /** Comma separated list of port numbers to use for remote debugging */
    @Value("${openpos.translate.external.process.debug.ports:}")
    private String remoteDebugPortValues;

    @Value("${openpos.translate.external.process.extra.vm.args:}")
    private String externalProcessExtraVmArgs;
    
    private String[] remoteDebugPorts = {};

    @Value("${openpos.translate.prefix.classpath}")
    private String prefixClassPath;
    
    @Value("${openpos.translate.postfix.classpath}")
    private String postfixClasspath;

    @Value("${openpos.translate.library.path}")
    private String libraryPath;
    
    private int externalProcessCount = 0;

    private Registry sharedRegistry;

    private Map<String, ITranslationManager> translationManagers = new HashMap<>();
    
    // Cache of Node-id to process mappings for accessing node JVM after startup
    private Map<String, Process> runningExternalProcesses = new HashMap<>();
    
    public static String getDefaultServiceName(String storeId, String workstationId) {
        return String.format("%s/%s-%s", ITranslationManager.class.getSimpleName(), storeId, workstationId);
    }

    @PostConstruct
    protected void init() {
        if (this.isExternalProcessEnabled()) {
            try {
                this.setSharedRegistry(LocateRegistry.createRegistry(this.getRmiRegistryPort()));
            } catch (RemoteException e) {
                logger.error("Failed to create rmi regsistry", e);
            }

            if (StringUtils.isNotBlank(remoteDebugPortValues)) {
                this.setRemoteDebugPorts(remoteDebugPortValues.split(","));
            }
        }
    }

    @Override
    public void startPreviouslyStarted() {
        File workingDir = new File(this.getAppWorkingDir());
        logger.info("The application working directory is set to {}", workingDir.getAbsolutePath());
        workingDir.mkdirs();
        if (workingDir.exists()) {
            File[] files = workingDir.listFiles();
            for (File file : files) {
                if (file.isDirectory() && Pattern.matches("\\d{5}-\\d{3}", file.getName())) {
                    if (file.list().length > 0) {
                        String storeId = file.getName().substring(0, 5);
                        String workstationId = file.getName().substring(6);
                        if (this.isExternalProcessEnabled()) {
                            startExternal(file, storeId, workstationId);
                        } else {
                            startInternal(file, storeId, workstationId);
                        }
                    } else {
                        logger.warn("Found directory that has no children files.  Not starting for {}", file.getName());
                    }
                }
            }
        } 
    }

    protected void terminate(String nodeId) {
        if (! this.isExternalProcessEnabled()) {
            logger.warn("Can't terminate node {} when running in internal mode", nodeId);
            return;
        }

        Process maybeRunningProcess = this.runningExternalProcesses.get(nodeId);
        if (maybeRunningProcess != null && maybeRunningProcess.isAlive()) {
            logger.info("Legacy node {} is alive, now stopping", nodeId);
            maybeRunningProcess.destroy();
            /* Should decrement this count in order to allow a subsequent remote debug connection to the VM, but
             * there is an issue that occurs when restarting the remote VM where it won't start if remote debug arguments
             * are passed to it.  I am guessing it pertains to the port that was allocated for remote debugging.
            if (this.externalProcessCount > 0) {
                this.externalProcessCount--;
            }
            */
        } else {
            logger.info("Legacy node {} is not currently alive, no need to terminate.", nodeId);
        }
        
    }
    
    public boolean restart(String nodeId) {
        if (! this.isExternalProcessEnabled()) {
            logger.warn("Restart of node {} is only supported when running in external mode", nodeId);
            return false;
        }
        
        boolean restarted = false;
        if (this.runningExternalProcesses.containsKey(nodeId)) {
            logger.info("Restarting legacy node {}...", nodeId);
            this.terminate(nodeId);
            this.start(nodeId);
            restarted = true;
        } else {
            logger.warn("Legacy node {} not found, nothing to restart", nodeId);
        }
        return restarted;
    }
    
    @Override
    public void start(String nodeId) {
        String storeId = NodeUtils.parseStoreId(nodeId);
        String workstationId = NodeUtils.parseWorkstationId(nodeId);
        File file = new File(this.getAppWorkingDir(), nodeId);
        if (this.isExternalProcessEnabled()) {
            startExternal(file, storeId, workstationId);
        } else {
            startInternal(file, storeId, workstationId);
        }

    }

    protected abstract void startHeadless();
    protected abstract ITranslationManager createTranslationManagerServer();
    protected abstract Class<?> getHeadlessWorkstationProcessClass();
    protected abstract String getExternalHeadlessWorkstationProcessServiceName(String storeId, String workstationId);
    protected abstract void generateStoreProperties(String directory, String storeId, String workstationId);
    
    /**
     * Invoked after {@link AbstractLegacyStartupService#generateStoreProperties(String, String, String)} to allow for initialization
     * of other configuration files and data.
     */
    protected void initOtherHeadlessConfiguration(String directory, String storeId, String workstationId) {
    }
    
    protected void startExternal(File file, String storeId, String workstationId) {
        try {
            file.mkdirs();
            logger.info("Starting external process for store: {}, workstation: {}", storeId, workstationId);
            String workingDir = file.getAbsolutePath();
            generateStoreProperties(workingDir, storeId, workstationId);
            initOtherHeadlessConfiguration(workingDir, storeId, workstationId);
            List<String> cmdLine = new ArrayList<>();
            String javaHome = System.getProperty("java.home");
            String os = System.getProperty("os.name");
            String classpath = System.getProperty("java.class.path");
            // add working dir to path
            
            classpath = workingDir  + java.io.File.pathSeparatorChar 
                            + (StringUtils.isNotEmpty(this.getPrefixClassPath()) ? (this.getPrefixClassPath()  + java.io.File.pathSeparatorChar):"")
                            + classpath
                            + (StringUtils.isNotEmpty(this.getPostfixClasspath()) ? (java.io.File.pathSeparatorChar) + this.getPostfixClasspath() :"");
                            
            logger.info("start POS with classpath: " + classpath );
            
            String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
            if (os.toLowerCase().contains("win")) {
                javaBin += ".exe";
            }

            cmdLine.add(javaBin);
            
            String[] remoteDebugPorts = this.getRemoteDebugPorts();
            if (remoteDebugPorts.length > 0) {
                if (this.getExternalProcessCount() < remoteDebugPorts.length) {
                    String port = remoteDebugPorts[this.getExternalProcessCount()];
                    cmdLine.add(String.format("-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=%s",port));
                    logger.debug("Assigned debug port {} to store/wkstn {}/{}", port, storeId, workstationId);
                } else {
                    logger.warn("Out of ports on the given list of ports via external.process.debug.ports, " +
                        "won't configure remote debugging for store/wkstn {}/{}", storeId, workstationId);
                }
            }
            
            if(StringUtils.isNotEmpty(libraryPath)) {
            		cmdLine.add("-Djava.library.path=" + libraryPath);
            }
            
            cmdLine.add("-Djava.net.preferIPv4Stack=true");
            
            if (StringUtils.isNotBlank(this.externalProcessExtraVmArgs)) {
                logger.trace("Adding VM arguments: [{}]", this.getExternalProcessExtraVmArgs());
                cmdLine.add(this.externalProcessExtraVmArgs);
            }
            
            // If you need to output where log4j is loading its config from, use this
            // cmdLine.add("-Dlog4j.debug");
            
            cmdLine.add("-cp");
            if (classpath.toLowerCase().contains("openpos-server.jar")) {
                // one jar
                String[] paths = classpath.split(System.getProperty("path.separator"));
                StringBuilder newPath = new StringBuilder();
                for (String path : paths) {
                    newPath.append(new File(path).getAbsolutePath()).append(System.getProperty("path.separator"));
                }
                cmdLine.add(newPath.toString());
                cmdLine.add(getClassToLaunch());                
            } else {
                // running in IDE or development environment
                cmdLine.add(classpath);
            }
            

            cmdLine.add(this.getHeadlessWorkstationProcessClass().getName());
            String processKey = String.format("%s-%s", storeId, workstationId);

            ProcessBuilder pb = new ProcessBuilder(cmdLine);
            pb.redirectErrorStream(true);
            pb.directory(new File(workingDir));
            // Redirect subprocess's input stream to this parent process's input stream.
            pb.redirectInput(Redirect.INHERIT);
            File redirectLogFile = new File(workingDir, "logs/process.log");
            redirectLogFile.getParentFile().mkdirs();
            pb.redirectOutput(redirectLogFile);
            logger.info("Starting process: {}", cmdLine);
            final Process process = pb.start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (process.isAlive()) {
                    logger.info("Destroying child process {}" + file.getName());
                    this.runningExternalProcesses.remove(processKey);
                    process.destroy();
                }
            }));
            if (process.isAlive()) {
                logger.info("Getting rmi interface for {}", file.getName());
                final int MAX_TRIES = 120;
                for (int i = 0; i < MAX_TRIES; i++) {
                    try {
                        RmiProxyFactoryBean factory = new RmiProxyFactoryBean();
                        factory.setServiceUrl(String.format("rmi://localhost:%d/%s", this.getRmiRegistryPort(),
                                this.getExternalHeadlessWorkstationProcessServiceName(storeId, workstationId)));
                        factory.setServiceInterface(ITranslationManager.class);
                        factory.afterPropertiesSet();
                        ITranslationManager remote = (ITranslationManager) factory.getObject();
                        remote.ping();
                        this.getTranslationManagers().put(file.getName(), remote);
                        logger.info("Established a connection with the remote interface for {}:{}", storeId, workingDir);
                        this.incrementExternalProcessCount();
                        this.runningExternalProcesses.put(processKey, process);
                        break;
                    } catch (RemoteAccessException e) {
                        this.getTranslationManagers().remove(file.getName());
                        if (i == 0) {
                            logger.info("The remote interface was not available.  Trying again in a second");
                        } else if (i == MAX_TRIES-1) {
                            logger.warn("Failed to establish a connection with the remote interface for {}:{}", storeId, workingDir);
                            process.destroy();
                            break;
                        }
                        Thread.sleep(1000);
                    }
                }
            } else {
                logger.warn("The launched process died unexpectly for {}", file.getName());
            }
        } catch (Exception e) {
            logger.error("Failed to start process", e);
        }
    }
    
    protected String getClassToLaunch() {
        return "org.springframework.boot.loader.JarLauncher";
    }

    protected void startInternal(File file, String storeId, String workstationId) {
        /**
         * Can only have one internal process
         */
        if (translationManagers.size() == 0) {
            file.mkdirs();
            String userDir = System.getProperty("user.dir");
            String currentNodeDirParentName = file.getParentFile().getName();
            File appWorkingDir = new File(this.getAppWorkingDir());
            String appDirName = appWorkingDir.getName();
            try {
                currentNodeDirParentName = file.getParentFile().getCanonicalFile().getName();
                appDirName = appWorkingDir.getCanonicalFile().getName();
            } catch (IOException ex) {
                logger.warn("", ex);
            }
            if (currentNodeDirParentName.equals(appDirName)) {
                logger.info("Starting internal process for store: {}, workstation: {} in working dir: {}", storeId, workstationId, userDir);
                generateStoreProperties(userDir, storeId, workstationId);
                initOtherHeadlessConfiguration(userDir, storeId, workstationId);
                this.startHeadless();
                translationManagers.put(file.getName(), this.createTranslationManagerServer());
            } else {
                logger.warn(
                        "Could not start the internal process for store: {}, workstation: {} because the node working directory needs to be: {}",
                        storeId, workstationId, this.getAppWorkingDir() + "/" + file.getName());
            }
        } else {
            logger.warn("Can only have one translation manager.  Registering {} under {}", file.getName(), translationManagers.keySet().iterator().next());
            translationManagers.put(file.getName(), translationManagers.values().iterator().next());
        }
    }
    
    @Override
    public ITranslationManager getTranslationManagerRef(String nodeId) {
        if (this.isExternalProcessEnabled()) {
            return translationManagers.get(nodeId);
        } else if (translationManagers.size() > 0) {
            return this.translationManagers.values().iterator().next();
        } else {
            return null;
        }
    }

    public boolean isExternalProcessEnabled() {
        return externalProcessEnabled;
    }

    public void setExternalProcessEnabled(boolean externalProcessEnabled) {
        this.externalProcessEnabled = externalProcessEnabled;
    }

    protected Registry getSharedRegistry() {
        return sharedRegistry;
    }

    protected void setSharedRegistry(Registry sharedRegistry) {
        this.sharedRegistry = sharedRegistry;
    }

    protected int getRmiRegistryPort() {
        return rmiRegistryPort;
    }

    protected void setRmiRegistryPort(int rmiRegistryPort) {
        this.rmiRegistryPort = rmiRegistryPort;
    }

    protected String[] getRemoteDebugPorts() {
        return remoteDebugPorts;
    }

    protected void setRemoteDebugPorts(String[] remoteDebugPorts) {
        this.remoteDebugPorts = remoteDebugPorts;
    }

    protected String getAppWorkingDir() {
        return appWorkingDir;
    }

    protected void setAppWorkingDir(String appWorkingDir) {
        this.appWorkingDir = appWorkingDir;
    }

    protected int getExternalProcessCount() {
        return externalProcessCount;
    }

    protected void setExternalProcessCount(int externalProcessCount) {
        this.externalProcessCount = externalProcessCount;
    }
    
    protected void incrementExternalProcessCount() {
        this.externalProcessCount++;
    }

    protected Map<String, ITranslationManager> getTranslationManagers() {
        return translationManagers;
    }

    protected void setTranslationManagers(Map<String, ITranslationManager> translationManagers) {
        this.translationManagers = translationManagers;
    }

    protected String getPrefixClassPath() {
        return prefixClassPath;
    }

    protected void setPrefixClassPath(String prefixClassPath) {
        this.prefixClassPath = prefixClassPath;
    }

    protected String getPostfixClasspath() {
        return postfixClasspath;
    }

    protected void setPostfixClasspath(String postfixClasspath) {
        this.postfixClasspath = postfixClasspath;
    }

	protected String getLibraryPath() {
		return libraryPath;
	}

	protected void setLibraryPath(String libraryPath) {
		this.libraryPath = libraryPath;
	}

    public String getExternalProcessExtraVmArgs() {
        return externalProcessExtraVmArgs;
    }

    public void setExternalProcessExtraVmArgs(String externalProcessExtraVmArgs) {
        this.externalProcessExtraVmArgs = externalProcessExtraVmArgs;
    }

}
