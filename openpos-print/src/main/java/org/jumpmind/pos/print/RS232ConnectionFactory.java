package org.jumpmind.pos.print;

import lombok.extern.slf4j.Slf4j;
import gnu.io.*;

import java.io.OutputStream;
import java.util.Map;

@Slf4j
public class RS232ConnectionFactory implements IConnectionFactory {

    private SerialPort serialPort;

    @Override
    public PrinterConnection open(Map<String, Object> settings) {
        java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier portIdentifier = portEnum.nextElement();
            System.out.println(portIdentifier.getName());

            if (portIdentifier.getName().equals("COM3")) {
                try {
                    CommPort commPort = portIdentifier.open("SerialMain4", 3000);
                    serialPort = (SerialPort) commPort;
                    serialPort.setSerialPortParams(19200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
                    OutputStream out = serialPort.getOutputStream();

                    PrinterConnection printerConnection = new PrinterConnection();
                    printerConnection.setOut(out);
                    return printerConnection;
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    public void close() {
        if (serialPort != null) {
            try {
                serialPort.close();
            } catch (Exception ex) {
                throw new PrintException("Failed to close serialPort: " + serialPort, ex);
            }
        }
    }


}
