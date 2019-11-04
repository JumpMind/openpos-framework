package org.jumpmind.pos.management;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.lang.ProcessBuilder.Redirect;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.management.OpenposManagementServerConfig.DeviceProcessConfig;
import org.jumpmind.pos.util.StreamCopier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DeviceProcessLauncher {
    public static final String PORT_FILENAME = ".openpos-port";
    private static final String END_OF_PROCESS_LOG_FOOTER = "\n\n" +
        "======================================================================\n" +
        "   END OF OUTPUT, see Device instance logs for remainder of logging\n" +
        "======================================================================";
    
    @Autowired
    OpenposManagementServerConfig config;
    
    @Autowired
    ScriptEngine groovyScriptEngine;
    
    @Autowired 
    private ApplicationContext appContext;
    
    @Autowired
    ProcessCommandBuilderFactory processCmdFactory;
    
    private Set<String> devicesWithShutdownHook = new HashSet<>();
    

    
    public DeviceProcessInfo launch(DeviceProcessInfo pi, long maxWaitMillis) throws DeviceProcessSetupException, DeviceProcessLaunchException {
        initializeWorkingDir(pi);
        File workingDir = getWorkingDir(pi.getDeviceId());
        ProcessCommandBuilder commandBuilder = processCmdFactory.make(config.getDeviceProcessConfig(pi));
        
        List<String> commandLine = commandBuilder.constructProcessCommandParts(pi);
        log.info("Device Process '{}' command line: {}", pi.getDeviceId(), String.join(" ", commandLine));
        
        ProcessBuilder processBuilder = constructProcessBuilder(pi, commandLine, workingDir);
        
        Process process = startProcess(pi, workingDir, processBuilder);
        pi.setProcess(process);
        optionallySetPort(pi);
        
        return pi;
    }

    public void kill(DeviceProcessInfo pi, long maxWaitMillis) {
        ProcessCommandBuilder commandBuilder = processCmdFactory.make(config.getDeviceProcessConfig(pi));
        List<String> killCommandParts = commandBuilder.constructKillCommandParts(pi);
        if (CollectionUtils.isNotEmpty(killCommandParts)) {
            ProcessBuilder builder = new ProcessBuilder(killCommandParts);
            builder.directory(getWorkingDir(pi.getDeviceId())).redirectErrorStream(true)
                .redirectInput(Redirect.INHERIT);
            try {
                log.info("Killing Device Process '{}' with command line: {}", pi.getDeviceId(), String.join(" ", killCommandParts));
                Process process = builder.start();
                if (maxWaitMillis > 0) {
                    process.waitFor(maxWaitMillis, TimeUnit.MILLISECONDS);
                }
                log.info("Kill command for Device Process '{}' exit code: {}", pi.getDeviceId(), process.exitValue());
            } catch (IllegalThreadStateException tse) {
                log.debug(String.format("Failed to get process exit code for Device Process '%s'", pi.getDeviceId()), tse);
            } catch (Exception ex) {
                log.warn(String.format("Error while killing Device Process '%s'", pi.getDeviceId()), ex);
            }
        } else if (pi.getProcess() != null && pi.getProcess().isAlive()) {
            pi.getProcess().destroy();
        }
    }
    
    protected void initializeWorkingDir(DeviceProcessInfo pi) throws DeviceProcessSetupException {
        if (isWorkingDirPresent(pi.getDeviceId())) {
            return;
        } else {
            log.info("Device Process '{}' is launching for the first time...", pi.getDeviceId());
        }

        File processWorkingDir = createWorkingDirectory(pi.getDeviceId());
        DeviceProcessConfig deviceProcessCfg = config.getDeviceProcessConfig(pi);
        
        if (StringUtils.isNotBlank(deviceProcessCfg.getInitializationScript())) {
            if (groovyScriptEngine == null) {
                processWorkingDir.delete();
                throw new DeviceProcessSetupException(
                    String.format("Failed to create groovy script engine when " +
                        "attempting to run initialization script '%s'. Is groovy on the classpath?", 
                        deviceProcessCfg.getInitializationScript()
                    )
                );
            }
 
            boolean executeFinished = false;
            Resource scriptRes = appContext.getResource(deviceProcessCfg.getInitializationScript());
            if (! scriptRes.exists()) {
                throw new DeviceProcessSetupException(
                    String.format("The Device Process '%s' initialization script '%s' was not found at the given location.", 
                        pi.getDeviceId(),
                        deviceProcessCfg.getInitializationScript()
                    )
                );
            }
            
            try (
                InputStream scriptStream = scriptRes.getInputStream();
                Reader scriptReader = new InputStreamReader(scriptStream);
            ) {
                try {
                    Bindings scriptVars = groovyScriptEngine.createBindings();
                    scriptVars.put("deviceId", pi.getDeviceId());
                    scriptVars.put("workingDir", processWorkingDir);
                    scriptVars.put("config", config);
                    scriptVars.put("deviceProcessConfig", config.getDeviceProcessConfig(pi));
                    scriptVars.put("log", log);
                    log.info("Executing Device Process '{}' initialization script '{}'...", 
                        pi.getDeviceId(), deviceProcessCfg.getInitializationScript());
                    Object scriptResult = groovyScriptEngine.eval(scriptReader, scriptVars);
                    log.info("Device Process '{}' initialization script completed with result: {}", 
                            pi.getDeviceId(), scriptResult);

                    if (scriptVars.get("processPort") != null) {
                        log.info("Port value detected and set from initialization script.  processPort={}", scriptVars.get("processPort"));
                        pi.setPort(Integer.valueOf(scriptVars.get("processPort").toString()));
                        if (config.getDeviceProcessConfig(pi).isReuseProcessPortEnabled()) {
                            this.savePort(pi);
                        }
                    }
                    executeFinished = true;
                } catch (DeviceProcessSetupException e) {
                    throw e;
                } catch (Exception ex) {
                    throw new DeviceProcessSetupException(
                        String.format("A failure occurred when executing Device Process '%s' initialization script '%s'.", 
                            pi.getDeviceId(),
                            deviceProcessCfg.getInitializationScript()
                        ),
                        ex
                    );
                }
            } catch (DeviceProcessSetupException dpex) {
                throw dpex;
            } catch (IOException e) {
                throw new DeviceProcessSetupException(
                    String.format("A failure occurred when loading Device Process '%s' initialization script '%s'.", 
                        pi.getDeviceId(),
                        deviceProcessCfg.getInitializationScript()
                    ),
                    e
                );
            } finally {
                if (!executeFinished) {
                    FileSystemUtils.deleteRecursively(processWorkingDir);
                }
            }
        }
    }

    protected Process startProcess(DeviceProcessInfo pi, File workingDir, ProcessBuilder processBuilder) {
        try {
            Process process = processBuilder.start();
            StreamCopier streamCopier = new StreamCopier(
                pi.getDeviceId(),
                process.getInputStream(),
                new FileOutputStream(new File(workingDir, config.getDeviceProcessConfig(pi).getProcessLogFilePath())),
                true,
                os -> {try {os.write(END_OF_PROCESS_LOG_FOOTER.getBytes());} catch (Exception ex) {log.warn("", ex);}}
            );
            streamCopier.start();
            pi.setStreamCopier(streamCopier);
            if (process.isAlive()) {
                addShutdownHook(pi);

                log.info("Device Process '{}' started", pi.getDeviceId());
                return process;
            } else {
                log.error("Launch failure for Device Process '{}'. Command line: {}", pi.getDeviceId(), String.join(" ", processBuilder.command()));
                throw new DeviceProcessLaunchException(
                        String.format("Device Process '%s' died unexpectedly.  See above for command line used.", pi.getDeviceId()));
            }
        } catch (IOException ex) {
            log.error("Launch failure for Device Process '{}'. Command line: {}", pi.getDeviceId(), String.join(" ", processBuilder.command()));
            throw new DeviceProcessLaunchException(
                String.format("Failed to launch Device Process for '%s'.  See above for command line used.", pi.getDeviceId()),
                ex
            );
        }
    }
    
    protected void addShutdownHook(DeviceProcessInfo dpi) {
        if (! this.devicesWithShutdownHook.contains(dpi.getDeviceId())) {
            this.devicesWithShutdownHook.add(dpi.getDeviceId());
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (this.devicesWithShutdownHook.contains(dpi.getDeviceId())) {
                    this.kill(dpi, 5000);
                }
            }));
        }
    }
    
    protected ProcessBuilder constructProcessBuilder(DeviceProcessInfo pi, List<String> commandLineParts, File workingDir) {
        File processLogfile = new File(workingDir, config.getDeviceProcessConfig(pi).getProcessLogFilePath());
        if (! processLogfile.exists()) {
            if (! processLogfile.getParentFile().exists()) {
                if (processLogfile.getParentFile().mkdirs()) {
                    log.info("Created directory '{}'", processLogfile.getParentFile().getAbsolutePath());
                }
            }
        }
        
        ProcessBuilder builder = new ProcessBuilder(commandLineParts);
        builder.directory(workingDir).redirectErrorStream(true)
            .redirectInput(Redirect.INHERIT);
        return builder;
    }
    
    
    public File getWorkingDir(String deviceId) {
        File mainWorkDir = new File(config.getMainWorkDirPath());
        File deviceProcessWorkDir = new File(mainWorkDir, deviceId);
        
        return deviceProcessWorkDir;
    }
    
    public boolean isWorkingDirPresent(String deviceId) {
        return getWorkingDir(deviceId).exists();
    }

    public boolean isNotWorkingDirPresent(String deviceId) {
        return ! isWorkingDirPresent(deviceId);
    }
    
    protected File createWorkingDirectory(String deviceId) {
        File deviceProcessWorkDir = getWorkingDir(deviceId);
        
        if (! deviceProcessWorkDir.exists()) {
            boolean success = false;
            Exception createDirEx = null;
            try {
                success = deviceProcessWorkDir.mkdirs();
            } catch (Exception ex) {
                success = false;
                createDirEx = ex;
            }
            if (success) {
                log.info("Device Process '{}' working dir created at: {}", deviceId, deviceProcessWorkDir.getAbsolutePath());
            } else {
                throw new DeviceProcessSetupException(
                    String.format("Failed to create Device Process working directory for device '%s' at location '%s'", 
                        deviceId, deviceProcessWorkDir.getAbsolutePath()
                    ), 
                    createDirEx
                );
            }
        }
        
        return deviceProcessWorkDir;
    }
    
    protected void savePort(DeviceProcessInfo pi) {
        File portFile = new File(this.getWorkingDir(pi.getDeviceId()), PORT_FILENAME);
        try(
            PrintWriter printWriter = new PrintWriter(portFile);
        ) {
            if (pi.getPort() != null) {
                printWriter.print(pi.getPort());
            }
        } catch (IOException e) {
            throw new DeviceProcessSetupException(String.format("Failed to create port file at '%s'.", portFile.getAbsolutePath()), e);
        }
    }
    
    protected void optionallySetPort(DeviceProcessInfo pi) {
        if (config.getDeviceProcessConfig(pi).isReuseProcessPortEnabled() && pi.getPort() == null) {
            File portFile = new File(this.getWorkingDir(pi.getDeviceId()), PORT_FILENAME);
            
            try (
                Scanner scanner = new Scanner(portFile);    
            ) {
                scanner.useDelimiter("\\Z");
                String portStr = scanner.next();
                pi.setPort(Integer.valueOf(portStr));
                log.debug("Port {} read from {} file for Device Process '{}'", portStr, PORT_FILENAME, pi.getDeviceId());
            } catch (FileNotFoundException fex) {
                throw new DeviceProcessLaunchException(
                        String.format("Expected to find port in '%s', but the file doesn't exist. " + 
                            "Cannot start Device Process '%s'.", portFile.getAbsolutePath(), pi.getDeviceId()));
            } catch (NumberFormatException ex) {
                throw new DeviceProcessLaunchException(
                    String.format("Failed to parse port from '%s'. " + 
                        "Cannot start Device Process '%s'. Error: %s", 
                        portFile.getAbsolutePath(), pi.getDeviceId(), ex.getMessage()));
            } 
        }
    }
    
}
