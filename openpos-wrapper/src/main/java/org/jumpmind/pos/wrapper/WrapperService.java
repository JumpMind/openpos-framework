package org.jumpmind.pos.wrapper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.jumpmind.pos.wrapper.Constants.Status;

import com.sun.jna.Platform;

public abstract class WrapperService {

    private static final Logger logger = Logger.getLogger(WrapperService.class.getName());

    protected WrapperConfig config;

    protected boolean keepRunning = true;

    protected Process child;

    protected BufferedReader childReader;

    private static WrapperService instance;

    public static WrapperService getInstance() {
        if (Platform.isWindows()) {
            instance = new WindowsService();
        } else {
            instance = new UnixService();
        }
        return instance;
    }

    public void loadConfig(String applHomeDir, String configFile, String jarFile) throws IOException {
        config = new WrapperConfig(applHomeDir, configFile, jarFile);
        setWorkingDirectory(config.getWorkingDirectory().getAbsolutePath());
    }

    public void start() {
        if (isRunning()) {
            throw new WrapperException(Constants.RC_SERVER_ALREADY_RUNNING, 0, "Server is already running");
        }

        System.out.println("Waiting for server to start");
        ArrayList<String> cmdLine = getWrapperCommand("exec");
        Process process = null;
        boolean success = false;
        int rc = 0;
        try {
            ProcessBuilder pb = new ProcessBuilder(cmdLine);
            pb.redirectErrorStream(true);
            process = pb.start();
            if (!(success = waitForPid(getProcessPid(process)))) {
                rc = process.exitValue();
            }
        } catch (IOException e) {
            rc = -1;
            System.out.println(e.getMessage());
        }

        if (success) {
            System.out.println("Started");
        } else {
            System.err.println(commandToString(cmdLine));
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    System.err.println(line);
                }
                reader.close();
            } catch (Exception e) {
            }
            throw new WrapperException(Constants.RC_FAIL_EXECUTION, rc, "Failed second stage");
        }
    }

    public void init() {
        execJava(false);
    }

    public void console() {
        if (isRunning()) {
            throw new WrapperException(Constants.RC_SERVER_ALREADY_RUNNING, 0, "Server is already running");
        }
        execJava(true);
    }

    protected void execJava(boolean isConsole) {
        try {
            LogManager.getLogManager().reset();
            new File(config.getLogFile()).getParentFile().mkdirs();
            WrapperLogHandler handler = new WrapperLogHandler(config.getLogFile(), config.getLogFileMaxSize(), config.getLogFileMaxFiles());
            handler.setFormatter(new WrapperLogFormatter());
            Logger rootLogger = Logger.getLogger("");
            rootLogger.setLevel(Level.parse(config.getLogFileLogLevel()));
            rootLogger.addHandler(handler);
        } catch (IOException e) {
            throw new WrapperException(Constants.RC_FAIL_WRITE_LOG_FILE, 0, "Cannot open log file " + config.getLogFile(), e);
        }

        try {
            int pid = getCurrentPid();
            writePidToFile(pid, config.getWrapperPidFile());
            logger.log(Level.INFO, "Started wrapper as PID " + pid);

            ArrayList<String> cmd = config.getCommand(isConsole);
            String cmdString = commandToString(cmd);
            boolean usingHeapDump = cmdString.indexOf("-XX:+HeapDumpOnOutOfMemoryError") != -1;
            logger.log(Level.INFO, "Working directory is " + config.getJavaProcessWorkingDirectory().getCanonicalPath());

            long startTime = 0;
            int startCount = 0;
            boolean startProcess = true, restartDetected = false;
            int serverPid = 0;

            while (keepRunning) {
                if (startProcess) {
                    logger.log(Level.INFO, "Executing " + cmdString);
                    if (startCount == 0) {
                        updateStatus(Status.START_PENDING);
                    }
                    startTime = System.currentTimeMillis();
                    ProcessBuilder pb = new ProcessBuilder(cmd);
                    pb.directory(config.getJavaProcessWorkingDirectory());
                    pb.redirectErrorStream(true);

                    try {
                        child = pb.start();
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Failed to execute: " + e.getMessage());
                        updateStatus(Status.STOPPED);
                        throw new WrapperException(Constants.RC_FAIL_EXECUTION, -1, "Failed executing server", e);
                    }

                    serverPid = getProcessPid(child);
                    logger.log(Level.INFO, "Started server as PID " + serverPid);
                    writePidToFile(serverPid, config.getServerPidFile());

                    if (startCount == 0) {
                        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
                        updateStatus(Status.RUNNING);
                    }
                    startProcess = false;
                    startCount++;
                } else {
                    try {
                        logger.log(Level.INFO, "Watching output of java process");
                        childReader = new BufferedReader(new InputStreamReader(child.getInputStream()));
                        String line = null;

                        while ((line = childReader.readLine()) != null || child.isAlive()) {
                            if (line != null) {
                                System.out.println(line);
                                logger.log(Level.INFO, line, "java");
                                if ((usingHeapDump && line.matches("Heap dump file created.*"))
                                        || (!usingHeapDump && line.matches("java.lang.OutOfMemoryError.*"))
                                        || line.matches(".*java.net.BindException.*")) {
                                    logger.log(Level.SEVERE, "Stopping server because its output matches a failure condition");
                                    child.destroy();
                                    childReader.close();
                                    stopProcess(serverPid, "application");
                                    break;
                                }
                                if (line.equalsIgnoreCase("Restarting")) {
                                    restartDetected = true;
                                }
                            }
                        }
                        logger.log(Level.INFO, "End of output from java process");
                    } catch (IOException e) {
                        logger.log(Level.SEVERE, "Error while reading from process");
                    }

                    if (restartDetected) {
                        logger.log(Level.INFO, "Restart detected");
                        restartDetected = false;
                        startProcess = true;
                    } else if (keepRunning) {
                        logger.log(Level.SEVERE, "Unexpected exit from server: " + exitValue(serverPid));
                        long runTime = System.currentTimeMillis() - startTime;
                        if (System.currentTimeMillis() - startTime < 7000) {
                            logger.log(Level.SEVERE, "Stopping because server exited too quickly after only " + runTime + " milliseconds");
                            shutdown(Constants.RC_SERVER_EXITED);
                            throw new WrapperException(Constants.RC_SERVER_EXITED, exitValue(serverPid), "Unexpected exit from server");
                        } else {
                            startProcess = true;
                        }
                    }
                }
            }
        } catch (Throwable ex) {
            // The default logging config doesn't show the stack trace here, so
            // include it in the message.
            try {
                logger.log(Level.SEVERE, "Exception caught.\r\n" + getStackTrace(ex));
                updateStatus(Status.STOPPED);
                throw new WrapperException(Constants.RC_SERVER_EXITED, child.exitValue(), "Exception caught.");
            } catch (Throwable ex2) {
                ex.printStackTrace();
            }
        }
    }

    private int exitValue(int pid) {
        try {
            return child.exitValue();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Killing the child process explicitly because we could not get an exit value");
            child.destroy();
            stopProcess(pid, "application");
            try {
                return child.exitValue();
            } catch (Exception ex2) {
                logger.log(Level.SEVERE, "Failed to get the exit value for the process.  Returning 1");
                return 1;
            }
        }
    }

    public void stop() {
        int symPid = readPidFromFile(config.getServerPidFile());
        int wrapperPid = readPidFromFile(config.getWrapperPidFile());
        if (!isPidRunning(symPid) && !isPidRunning(wrapperPid)) {
            throw new WrapperException(Constants.RC_SERVER_NOT_RUNNING, 0, "Server is not running");
        }
        System.out.println("Waiting for server to stop");
        if (!(stopProcess(wrapperPid, "wrapper") && stopProcess(symPid, "application"))) {
            throw new WrapperException(Constants.RC_FAIL_STOP_SERVER, 0, "Server did not stop");
        }
        System.out.println("Stopped");
    }

    protected boolean stopProcess(int pid, String name) {
        killProcess(pid, false);
        if (waitForPid(pid)) {
            killProcess(pid, true);
            if (waitForPid(pid)) {
                System.out.println("ERROR: '" + name + "' did not stop");
                return false;
            }
        }
        return true;
    }

    protected void shutdown(int code) {
        if (keepRunning) {
            keepRunning = false;
            new Thread() {
                @Override
                public void run() {
                    logger.log(Level.INFO, "Stopping server");
                    child.destroy();
                    try {
                        childReader.close();
                    } catch (IOException e) {
                    }
                    logger.log(Level.INFO, "Stopping wrapper");
                    deletePidFile(config.getWrapperPidFile());
                    deletePidFile(config.getServerPidFile());
                    updateStatus(Status.STOPPED);
                    System.exit(code);
                }
            }.start();
        } else {
        	logger.log(Level.INFO, "Shutdown was requested, but it should have already been shutdown");
        }
    }

    public void restart() {
        if (isRunning()) {
            stop();
        }
        start();
    }

    public void relaunchAsPrivileged(String cmd, String args) {
    }

    public void status() {
        boolean isRunning = isRunning();
        System.out.println("Installed: " + isInstalled());
        System.out.println("Running: " + isRunning);
        if (isRunning) {
            System.out.println("Wrapper PID: " + readPidFromFile(config.getWrapperPidFile()));
            System.out.println("Server PID: " + readPidFromFile(config.getServerPidFile()));
        }
    }

    public boolean isRunning() {
        return isPidRunning(readPidFromFile(config.getWrapperPidFile())) || isPidRunning(readPidFromFile(config.getServerPidFile()));
    }

    public int getWrapperPid() {
        return readPidFromFile(config.getWrapperPidFile());
    }

    public int getServerPid() {
        return readPidFromFile(config.getServerPidFile());
    }

    protected String commandToString(ArrayList<String> cmd) {
        StringBuilder sb = new StringBuilder();
        for (String c : cmd) {
            sb.append(c).append(" ");
        }
        return sb.toString();
    }

    protected ArrayList<String> getWrapperCommand(String arg) {
        ArrayList<String> cmd = new ArrayList<String>();
        String quote = getWrapperCommandQuote();
        cmd.add(quote + config.getJavaCommand() + quote);
        cmd.add("-Djava.io.tmpdir=" + quote + System.getProperty("java.io.tmpdir") + quote);
        cmd.add("-Duser.dir=" + quote + System.getProperty("user.dir") + quote);
        cmd.add("-jar");
        cmd.add(quote + config.getWrapperJarPath() + quote);
        cmd.add(arg);
        cmd.add(quote + config.getConfigFile() + quote);
        return cmd;
    }

    protected ArrayList<String> getPrivilegedCommand() {
        ArrayList<String> cmd = new ArrayList<String>();
        String quote = getWrapperCommandQuote();
        cmd.add(quote + config.getJavaCommand() + quote);
        return cmd;
    }

    protected String getWrapperCommandQuote() {
        return "";
    }

    protected int readPidFromFile(String filename) {
        int pid = 0;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            pid = Integer.parseInt(reader.readLine());
            reader.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pid;
    }

    protected void writePidToFile(int pid, String filename) {
        try {
            new File(filename).getParentFile().mkdirs();
            FileWriter writer = new FileWriter(filename, false);
            writer.write(String.valueOf(pid));
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void deletePidFile(String filename) {
        new File(filename).delete();
    }

    protected boolean waitForPid(int pid) {
        int seconds = 0;
        while (seconds <= 5) {
            System.out.print(".");
            if (!isPidRunning(pid)) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            seconds++;
        }
        System.out.println("");
        return isPidRunning(pid);
    }

    protected void updateStatus(Status status) {
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    class ShutdownHook extends Thread {
        public void run() {
            shutdown(0);
        }
    }

    public abstract void install();

    public abstract void uninstall();

    public abstract boolean isInstalled();

    public abstract boolean isPrivileged();

    protected abstract boolean setWorkingDirectory(String dir);

    protected abstract int getProcessPid(Process process);

    protected abstract int getCurrentPid();

    protected abstract boolean isPidRunning(int pid);

    protected abstract void killProcess(int pid, boolean isTerminate);

}
