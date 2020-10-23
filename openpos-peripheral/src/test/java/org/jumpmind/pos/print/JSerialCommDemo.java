package org.jumpmind.pos.print;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

public class JSerialCommDemo {

    public static void main(String[] args) throws Exception {

        for (String key : System.getProperties().stringPropertyNames()) {
            System.out.println(key + " " + System.getProperty(key));
        }
        SerialPort port = SerialPort.getCommPort("COM3");
        port.openPort();

        InputStream in = port.getInputStream();

        OutputStream out = port.getOutputStream();
        out.write("Hello\n".getBytes());

        port.closePort();
    }
}
