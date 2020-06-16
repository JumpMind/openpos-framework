package org.jumpmind.pos.print;

import lombok.extern.slf4j.Slf4j;
import gnu.io.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.Map;

@Slf4j
public class RS232ConnectionFactory implements IConnectionFactory {

    public final static String PORT_NAME = "portName";
    public final static String BAUD_RATE = "baudRate";
    public final static String DATA_BITS = "dataBits";
    public final static String STOP_BITS = "stopBits";
    public final static String PARITY = "parity";
    public final static String CONNECT_TIMEOUT = "connectTimeout";

    public final static String DLL_NAME = "rxtxSerial.dll";

    @Override
    public PeripheralConnection open(Map<String, Object> settings) {
        checkRxTxDll(settings);
        String portName = (String) settings.get(PORT_NAME);
        if (StringUtils.isEmpty(portName)) {
            throw new PrintException("No portName was specified.  Something like COM1 needs " +
                    "to be specified for RS232 connections. " + settings);
        }

        log.info("Connecting to port " + portName + "...");

        try {
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
            PeripheralConnection connection = openSerialPort(settings, portIdentifier);
            log.info("Successfully connected to port " + portName + " for printing.");
            return connection;
        } catch (Exception ex) {
            throw new PrintException("Failed to open serial port for printing " + portName + " settings=" + settings, ex);
        }


    }

    private void checkRxTxDll(Map<String, Object> settings) {
        String dllSourcePath = (String) settings.get("dllSourcePath");
        String dllTargetPath = (String) settings.get("dllTargetPath");

        if (StringUtils.isEmpty(dllSourcePath)) {
            dllSourcePath = "";
        }
        if (StringUtils.isEmpty(dllTargetPath)) {
            dllTargetPath = ".";
        }

        String sourceDll = dllSourcePath + "/" + DLL_NAME;
        String targetDll = dllTargetPath + "/" + DLL_NAME;

        try {
            if (!new File(targetDll).exists()) {
                URL sourceDllURL = null;
                if (new File(sourceDll).exists()) {
                    sourceDllURL = new File(sourceDll).toURI().toURL();
                } else {
                    sourceDllURL = Thread.currentThread().getContextClassLoader().getResource(sourceDll);
                    if (sourceDllURL == null && sourceDll.startsWith("/")) {
                        sourceDllURL = Thread.currentThread().getContextClassLoader().getResource(sourceDll.substring(1));
                    }
                }

                if (sourceDllURL != null) {
                    log.info("Attempting to copy DLL from " + sourceDllURL + " to " + new File(targetDll).getAbsolutePath());
                    FileUtils.copyInputStreamToFile(sourceDllURL.openStream(), new File(targetDll));
                } else {
                    log.warn("Source DLL was not found \"" + sourceDll + "\"");
                }
            }
        } catch (Exception ex) {
            throw new PrintException("Failed to copy sourceDll=" + sourceDll +
                    " to targetDll=" + targetDll);
        }
    }

    private PeripheralConnection openSerialPort(Map<String, Object> settings, CommPortIdentifier portIdentifier) throws PortInUseException, UnsupportedCommOperationException, IOException {
        int connectTimeout = getIntValue(CONNECT_TIMEOUT, 10000, settings);
        int baudRate = getIntValue(BAUD_RATE, 19200, settings);
        int dataBits = getIntValue(DATA_BITS, SerialPort.DATABITS_8, settings);
        int stopBits = getIntValue(STOP_BITS, SerialPort.STOPBITS_1, settings);
        int parity = getIntValue(PARITY, SerialPort.PARITY_NONE, settings);

        String owner = RS232ConnectionFactory.class.getName();
        CommPort commPort = portIdentifier.open(owner, connectTimeout);
        SerialPort serialPort = (SerialPort) commPort;
        serialPort.setSerialPortParams(baudRate, dataBits, stopBits, parity);
        OutputStream out = serialPort.getOutputStream();
        InputStream in = serialPort.getInputStream();

        PeripheralConnection peripheralConnection = new PeripheralConnection();
        peripheralConnection.setRawConnection(serialPort);
        peripheralConnection.setOut(out);
        peripheralConnection.setIn(in);
        return peripheralConnection;
    }

    private Integer getIntValue(String key, Integer defaultValue, Map<String, Object> settings) {
        Object value = settings.get(key);
        if (value instanceof String) {
            return Integer.parseInt(((String) value).trim());
        } else if (value instanceof  Integer) {
            return (Integer) value;
        } else if (value == null) {
            return defaultValue;
        } else {
            throw new PrintException("Unrecognized type for key=" + key + " value=" + value);
        }
    }

    @Override
    public void close(PeripheralConnection peripheralConnection) {
        if (peripheralConnection.getRawConnection() instanceof SerialPort) {
            SerialPort serialPort = (SerialPort)peripheralConnection.getRawConnection();
            serialPort.close();
        }
    }
}
