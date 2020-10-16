package org.jumpmind.pos.print;

import com.fazecast.jSerialComm.SerialPort;

import java.io.InputStream;
import java.io.OutputStream;

public class JSerialCommDemo {

    public static void main(String[] args) throws Exception {
        SerialPort port = SerialPort.getCommPort("COM6");
        port.openPort();

        InputStream in = port.getInputStream();

        OutputStream out = port.getOutputStream();
        out.write("Hello".getBytes());

        port.closePort();
    }
}
