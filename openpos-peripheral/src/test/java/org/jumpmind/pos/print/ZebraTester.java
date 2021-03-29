package org.jumpmind.pos.print;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.net.Socket;

public class ZebraTester {

    private static OutputStream stream;

    public static void main(String[] args) throws Exception {
        Socket socket = new Socket("192.168.1.66", 6101);
        stream = socket.getOutputStream();

//        command("! U1 setvar \"device.languages\" \"line_print\"\r\n");

        BufferedImage image =
                ImageIO.read(Thread.currentThread().getContextClassLoader().getResource("images/jumpmind-logo.png").openStream());
        command("! 0 200 200 210 1");
        ZebraImagePrinter imagePrinter = new ZebraImagePrinter();
        String imageCommand = imagePrinter.getZebraGraphicsCommand(image);
        System.out.println(imageCommand);
        command(imageCommand);
        command("PRINT");

        command("! U1 SETLP 5 2 46" +
                " JUMPMIND COMMERCE\r\n" +
                "! U1 SETLP 5 0 24\r\n" +
                " 123 Castle Drive, Kingston, RI 02881\r\n" +
                " (401) 555-4CUT\r\n" +
                "! U1 SETLP 7 0 24\r\n" +
                " 4:20 PM Thursday, June 04, 2020 Store: 142\r\n" +
                " Order Number: #59285691\r\n" +
                " Status: ! U1 SETSP 10\r\n" +
                "INCOMPLETE ! U1 SETSP 0\r\n" +
                "Item Description Quant. Price Subtotal Tax\r\n" +
                "1211 45\" Buckram 5 yds @ $3.42/yd $17.10 Y\r\n" +
                "Z121 60\" Blue Silk 10 yds@ $15.00/yd $150.00 N\r\n" +
                "Z829 60\" Muslin 20 yds@ $1.00/yd $20.00 Y\r\n" +
                " SUBTOTAL: $187.10\r\n" +
                " RHODE ISLAND SALES TAX 7.00%: $2.60\r\n" +
                "       TOTAL: $189.70\r\n" +
                "! U1 SETLP 7 1 48\r\n" +
                " PLEASE BRING THIS RECEIPT TO THE CASHIER\r\n" +
                "! U1 SETBOLD 2\r\n" +
                " WITH THE REST OF YOUR PURCHASES.\r\n"  +
                "! U1 SETBOLD 0\r\n" +
                "! U1 CENTER\r\n" +
                "! U1 B 128 1 2 100 0 0 A123456789012334\r\n\r\n\r\n");


        stream.flush();
        Thread.sleep(1500);
        stream.close();


//        command ("PRINT\r\nAlright?\r\n");
//        command("! U1 SETBOLD 2");
//        command("bold text ");
//        command("! U1 SETBOLD 0");
//        command("regular.\n");
//        command("! U1 SETLP 5 2 46");
//        command(" AURORA'S FABRIC SHOP");
//        command("! U1 SETLP 0 0 24\n");
//        command("And back to regular.\n");
//

//                command("! U1 setvar \"device.languages\" \"line_print\"\r\n");
//        command("! 0 200 200 210 1");
//        command("TEXT 7 0 0 0 12345678901234567890");
//        command("TEXT 7 0 0 30 Hi here line 2");
//        command("PRINT");
//
//        command("! 0 200 200 210 1");
//        command("TEXT 7 0 0 60 1234567890123456789012345678901234567890");
//        command("TEXT 7 0 0 90 testint line 4");
//        command("TEXT 4 0 0 120 Diyvk===");
//        command("TEXT 7 0 0 170 normal again");
//        command("PRINT");

    }

    public static void command(String cmd) throws Exception {
        stream.write((cmd + "\r\n").getBytes());
    }
}
