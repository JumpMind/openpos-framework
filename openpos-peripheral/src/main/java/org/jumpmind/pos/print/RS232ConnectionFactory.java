package org.jumpmind.pos.print;

import lombok.extern.slf4j.Slf4j;
import gnu.io.*;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Slf4j
public class RS232ConnectionFactory implements IConnectionFactory {

    public final static String PORT_NAME = "portName";
    public final static String BAUD_RATE = "baudRate";
    public final static String DATA_BITS = "dataBits";
    public final static String STOP_BITS = "stopBits";
    public final static String PARITY = "parity";
    public final static String CONNECT_TIMEOUT = "connectTimeout";

    @Override
    public PeripheralConnection open(Map<String, Object> settings) {
        String portName = (String) settings.get(PORT_NAME);
        if (StringUtils.isEmpty(portName)) {
            throw new PrintException("No PORT_NAME was specified.  Something like COM1 needs " +
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

        PeripheralConnection peripheralConnection = new PeripheralConnection();
        peripheralConnection.setRawConnection(serialPort);
        peripheralConnection.setOut(out);
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
