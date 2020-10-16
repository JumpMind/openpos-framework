package org.jumpmind.pos.print;

import com.fazecast.jSerialComm.SerialPort;
import gnu.io.CommPortIdentifier;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

@Slf4j
public class RS232JSerialCommConnectionFactory implements IConnectionFactory {

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
            throw new PrintException("No portName was specified.  Something like COM1 needs " +
                    "to be specified for RS232 connections. " + settings);
        }

        log.info("Connecting to port '" + portName + "'...");

        try {
            PeripheralConnection connection = openSerialPort(portName, settings);
            log.info("Successfully connected to port " + portName);
            return connection;
        } catch (Exception ex) {
            if (ex instanceof PrintException) {
                throw (PrintException)ex;
            }
            throw new PrintException("Failed to open serial port " + portName + " settings=" + settings, ex);
        }
    }

    protected PeripheralConnection openSerialPort(String portName, Map<String, Object> settings) {
        SerialPort port = SerialPort.getCommPort(portName);

        int connectTimeout = getIntValue(CONNECT_TIMEOUT, 10000, settings);
        int baudRate = getIntValue(BAUD_RATE, 19200, settings);
        int dataBits = getIntValue(DATA_BITS, gnu.io.SerialPort.DATABITS_8, settings);
        int stopBits = getIntValue(STOP_BITS, gnu.io.SerialPort.STOPBITS_1, settings);
        int parity = getIntValue(PARITY, gnu.io.SerialPort.PARITY_NONE, settings);

        port.setBaudRate(baudRate);
        port.setNumDataBits(dataBits);
        port.setNumStopBits(stopBits);
        port.setParity(parity);
        port.setComPortTimeouts(SerialPort.TIMEOUT_NONBLOCKING, connectTimeout, connectTimeout);

        boolean success = port.openPort();
        if (!success) {
            throw new PrintException("Failed to open serial port " + portName + " settings=" + settings + " - reason unknown.");
        }

        PeripheralConnection connection = new PeripheralConnection();
        connection.setIn(port.getInputStream());
        connection.setOut(port.getOutputStream());
        connection.setRawConnection(port);
        return connection;
    }

    @Override
    public void close(PeripheralConnection peripheralConnection) {
        if (peripheralConnection.getRawConnection() instanceof SerialPort) {
            gnu.io.SerialPort serialPort = (gnu.io.SerialPort)peripheralConnection.getRawConnection();
            serialPort.close();
        }
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
}
