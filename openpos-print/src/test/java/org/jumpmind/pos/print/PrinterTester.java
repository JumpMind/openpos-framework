package org.jumpmind.pos.print;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.POSPrinterControl114;
import jpos.config.JposEntry;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * A developer tool for testing a printer with openpos capabilities.
 */
public class PrinterTester {

    private static IOpenposPrinter createPrinter() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("printerCommandLocations", "esc_p.properties, epson.properties");
        settings.put("printerCommandLocations", "esc_p.properties,epson.properties");
        settings.put("connectionClass", "org.jumpmind.pos.print.UsbConnectionFactory");
//        settings.put("connectionClass", "org.jumpmind.pos.print.SocketConnectionFactory");
//        settings.put("hostName", "192.168.42.181");
//        settings.put("port", "9100");
        settings.put("printWidth", "46");
        settings.put("usbVendorId", 0x0404); // NCR
//        settings.put("usbVendorId", 0x04b8); // EPSON
//        settings.put("usbVendorId", 0x08a6); // TOSHIBA
        settings.put("usbProductId", "ANY");


        IOpenposPrinter printer = null;
        try {
            printer = (IOpenposPrinter) Class.forName(EscpPOSPrinter.class.getName()).newInstance();
            printer.init(settings);
            printer.open("printerName", null);
        } catch (Exception ex) {
            ex.printStackTrace();;
        }
        return printer;
    }

    public static void main(String[] args) throws Exception {
        
        try {
            IOpenposPrinter printer = createPrinter();

            // reset method.
//            printer.printNormal(0, printer.getCommand(PrinterCommands.ESC_P_MODE));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FORMAT_NORMAL));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.ALIGN_LEFT));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));

            printer.getPrinterConnection().getOut().write(new byte[] {0x1B, 0x40}); // ESCP reset.
            printer.getPrinterConnection().getOut().flush();


//            printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "Initial print on receipt printer.\n");

            String BOLD = printer.getCommand(PrinterCommands.FORMAT_BOLD);
            String NORMAL = printer.getCommand(PrinterCommands.FORMAT_NORMAL);


            // printer.printSlip(BOLD + "FOR DEPOSIT ONLY" + NORMAL + "\nPrinting on the slip printer.\n A second line here.\n\nAccount #12342346456\n", 0);

//            printer.getPrinterConnection().getOut().write(new byte[] {0x1B, 0x66, 1, 2}); // wait for one minute for a slip, and start printing .2 seconds after slip detected.
//            printer.getPrinterConnection().getOut().write(new byte[] {0x1B, 0x63, 0x30, 4}); // select slip
//            printer.getPrinterConnection().getOut().flush();
//
//            printer.printNormal(0, BOLD + "FOR DEPOSIT ONLY" + NORMAL + "\nPrinting on the slip printer.\n A second line here.\n\nAccount #12342346456\n");
//
//            printer.printNormal(0, StringUtils.repeat("\n", 100));
//            Thread.sleep(2000);
//            printer.getPrinterConnection().getOut().write(new byte[] {0x1B, 0x63, 0x30, 1}); // select receipt
//            printer.getPrinterConnection().getOut().flush();

            printer.printNormal(0, "This is for the receipt.");


//            printer.printSlip(BOLD + "FOR DEPOSIT ONLY" + NORMAL + "\nPrinting on the slip printer.\n A second line here.\n\nAccount #12342346456\n", 30000);

//            printer.printNormal(POSPrinterConst.PTR_S_RECEIPT, "Back to receipt printer.\n");


//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/header-image.png"));

//            StringBuilder buffer = new StringBuilder(128);
//

//
//            buffer.append(BOLD).append("6/18/2019 5:03PM");
//            buffer.append(NORMAL).append(" Helped by ");
//            buffer.append(BOLD).append("Sara ");
//            buffer.append(NORMAL).append("(20)");
//            buffer.append("\n");
//            printer.printNormal(0, buffer.toString());
//            buffer.setLength(0);
//
//            buffer.append(BOLD).append("559 ");
//            buffer.append(NORMAL).append("Reg ");
//            buffer.append(BOLD).append("3 ");
//            buffer.append(NORMAL).append("Trans ");
//            buffer.append(BOLD).append("234238 ").append(NORMAL);
//            buffer.append("\n");
//            printer.printNormal(0, buffer.toString());
//            buffer.setLength(0);
//
////            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/gift-receipt-header2.png"));
//            printer.printNormal(0, "\n\n");
////            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/items-divider.png"));
//            printer.printNormal(0, "\n\n");
//
//            printer.printNormal(0, NORMAL);
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));
//
//// Toshiba: 42 chars wide.
//            buffer.append("777777777 Item Affected by Tax      50.00 \n");
//            buffer.append("002900184 VETCO-SERVICES (T)       29.99 T\n");
//            buffer.append(BOLD + "  50% COUPON DISCOUNT You saved 30.00\n" + NORMAL);
//            buffer.append("002900184 VETCO-SERVICES (T)       59.99 T\n");
////            lineSpacing1AndHalf();
//            buffer.append("002900184 VETCO-SERVICES (T)       29.99 T\n\n");
//            //printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_1_AND_HALF));
//            //lineSpacing1();
//            buffer.append("                  Subtotal          119.97\n");
////            lineSpacing1AndHalf();
//            buffer.append("         CA Sales Tax 6.0%            7.20\n\n");
//            printer.printNormal(0, buffer.toString());
//            buffer.setLength(0);
//
//            printer.printNormal(0, "               " + printer.getCommand(PrinterCommands.FONT_SIZE_LARGE) + "TOTAL  127.17\n\n");
//
////            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/tenders-divider.png"));
//
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));
//
//            printer.printNormal(0, BOLD+"MASTERCARD                         127.17\n" + NORMAL);
//            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_TIGHT));
//
//            printer.printNormal(0, "PURCHASE - APPROVED                  \n");
//            printer.printNormal(0, "MasterCard Credit Entry Method: Swipe\n");
//            printer.printNormal(0, "CARD #: XXXXXXXXXXXX6158\n");
//            printer.printNormal(0, "AUTH CODE: 952823  SEQ: 029620\n");
//            printer.printNormal(0, "Ticket #: 31962508244326161\n");
//            printer.printNormal(0, "TransID: 185191625082443185\n");
//
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));
//
////            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/divider.png"));
//
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_LARGE)+printer.getCommand(PrinterCommands.ALIGN_CENTER) + "3 Items\n");
//
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.FORMAT_NORMAL));
//            printer.printNormal(0, printer.getCommand(PrinterCommands.ALIGN_LEFT));
//
//            printer.printNormal(0, "\n");
//
//            printer.printNormal(0,"Pals Rewards Number: 462411413\n");
//
//            printer.printNormal(0,BOLD +"You saved 30.00 today.\n\n" + NORMAL);
//
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Thank you for shopping at JumpMind!\n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Questions or comments?\n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Call us at 888-942-5867\n\n");
//
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"JumpMind provides professional \n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"implementation and consulting services \n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"for all the software we offer. \n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"For more go to "+ BOLD + "jumpmind.com" + NORMAL + "\n\n");
//
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"enterprise pos, mobile, customer facing,\n self-checkout, with complete retail grid: \n");
//
////            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/marketing-image.png"));
//
//
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"\nNu Commerce is the new robust, \nenterprise retail solution. \n\n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Schedule a " + BOLD + "demo " + NORMAL + "today with\n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"our sales team. Shoot us an email at  \n");
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+ BOLD + "sales@jumpmind.com" + NORMAL + "\n\n");
//
//            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+ BOLD + "Brought to you by" + NORMAL + "\n");
//
////            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/jumpmind-logo.png"));
//
//            printer.printNormal(0, "\n\n");
//
//            printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT,"380502001835720192324", POSPrinterConst.PTR_BCS_Code128, 50, 150,
//                    POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);

            printer.printNormal(0, "\n\n\n\n\n\n");
            printer.cutPaper(100);
            printer.close();

        } catch (Exception ex) {
            ex.printStackTrace();
            if (ex instanceof JposException) {
                JposException jposEx = (JposException) ex;
                if (jposEx.getOrigException() != null) {
                    jposEx.getOrigException().printStackTrace();;
                }
            }
        }
    }

    // TODO temp.
    public static final int ESC = 27;
//    public static final String FONT_SIZE_MEDIUM = codes(ESC, 88, 1, 25, 1);
    public static final String FONT_SIZE_MEDIUM = codes(ESC, 0x58, 0x1, 25, 0x1);


    private static String codes(int... codes) {
        String s = "";
        for (int i : codes) {
            s += String.valueOf((char)i);
        }
        return s;
    }
}
