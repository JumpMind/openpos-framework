package org.jumpmind.pos.print;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class CheckoutScaleTester {

        static String serialPortName = "/dev/tty.usbserial-1410";
//    static String serialPortName = "COM1";

    public static void main(String[] args) throws Exception {

        RS232JSerialCommConnectionFactory factory = new RS232JSerialCommConnectionFactory();

        java.util.Map<String, Object> settings = new HashMap<>();
        settings.put(RS232JSerialCommConnectionFactory.PORT_NAME, serialPortName);
//        settings.put(RS232JSerialCommConnectionFactory.CONNECT_TIMEOUT, 2000);
        settings.put(RS232JSerialCommConnectionFactory.BAUD_RATE, 9600);
//        settings.put(RS232JSerialCommConnectionFactory.DATA_BITS, 8);
//        settings.put(RS232JSerialCommConnectionFactory.PARITY, SerialPort.PARITY_NONE);

        PeripheralConnection connection = factory.open(settings);

        OutputStream out = connection.getOut();

//        out.write('W');
//        out.flush();

//        out.write(0x0E);
//        out.flush();


//        out.close();

        InputStream in = connection.getIn();

        Thread.sleep(300);

        // swwwww.wwxcc
        // s= sign (either `space or -)
        // w=weight digits, with leading spaces.
        // x =

        while (true) {
            out.write('W');
//            out.write('\r');
            out.flush();
            Thread.sleep(500);
            System.out.println(in.available());
            while (in.available() > 0) {
                int byteRead = in.read();
                System.out.println("Byte read " + (char)byteRead);
            }
        }




    }


}
