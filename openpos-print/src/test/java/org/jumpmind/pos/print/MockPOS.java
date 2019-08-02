package org.jumpmind.pos.print;

import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.POSPrinterControl114;

import java.lang.reflect.Field;

public class MockPOS {

    public static void main(String[] args) throws Exception {


        try {
            POSPrinterControl114 jposPrinter = (jpos.POSPrinterControl114) new POSPrinter();

            jposPrinter.open("EpsonPrinter");
            jposPrinter.claim(100);

            // grr...
            Field sevice19Field = jposPrinter.getClass().getDeclaredField("service19");
            sevice19Field.setAccessible(true);
            IOpenposPrinter printer = (IOpenposPrinter) sevice19Field.get(jposPrinter);

            printer.setDeviceEnabled(true);

            // reset method.
            printer.printNormal(0, printer.getCommand(PrinterCommands.ESC_P_MODE));
            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
            printer.printNormal(0, printer.getCommand(PrinterCommands.FORMAT_NORMAL));
            printer.printNormal(0, printer.getCommand(PrinterCommands.ALIGN_LEFT));
            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));


//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/chris.png"));
//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/chris.png"));
//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/chris.png"));
//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/chris.png"));
//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/chris.png"));
//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/chris.png"));
//            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/chris.png"));
//
//            printer.printNormal(0, "\n\n\n\n\n\n\n\n");
//            printer.cutPaper(100);
//            System.exit(0);

            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/header-image.png"));

            StringBuilder buffer = new StringBuilder(128);

            String BOLD = printer.getCommand(PrinterCommands.FORMAT_BOLD);
            String NORMAL = printer.getCommand(PrinterCommands.FORMAT_NORMAL);

            buffer.append(BOLD).append("6/18/2019 5:03PM");
            buffer.append(NORMAL).append(" Helped by ");
            buffer.append(BOLD).append("Sara ");
            buffer.append(NORMAL).append("(20)");
            buffer.append("\n");
            printer.printNormal(0, buffer.toString());
            buffer.setLength(0);

            buffer.append(BOLD).append("559 ");
            buffer.append(NORMAL).append("Reg ");
            buffer.append(BOLD).append("3 ");
            buffer.append(NORMAL).append("Trans ");
            buffer.append(BOLD).append("234238 ").append(NORMAL);
            buffer.append("\n");
            printer.printNormal(0, buffer.toString());
            buffer.setLength(0);

            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/items-divider.png"));

            printer.printNormal(0, NORMAL);
            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));

            buffer.append("777777777 Item Affected by Tax      50.00 \n");
            buffer.append("002900184 VETCO-SERVICES (T)        29.99 T\n");
            buffer.append(BOLD + "  50% COUPON DISCOUNT You saved 30.00\n" + NORMAL);
            buffer.append("002900184 VETCO-SERVICES (T)        59.99 T\n");
//            lineSpacing1AndHalf();
            buffer.append("002900184 VETCO-SERVICES (T)        29.99 T\n\n");
            //printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_1_AND_HALF));
            //lineSpacing1();
            buffer.append("                   Subtotal          119.97\n");
//            lineSpacing1AndHalf();
            buffer.append("          CA Sales Tax 6.0%            7.20\n\n");
            printer.printNormal(0, buffer.toString());
            buffer.setLength(0);

            printer.printNormal(0, "               " + printer.getCommand(PrinterCommands.FONT_SIZE_LARGE) + "TOTAL  127.17\n\n");

            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/tenders-divider.png"));

            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));

            printer.printNormal(0, BOLD+"MASTERCARD                         127.17\n" + NORMAL);
            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_TIGHT));

            printer.printNormal(0, "PURCHASE - APPROVED                  \n");
            printer.printNormal(0, "MasterCard Credit Entry Method: Swipe\n");
            printer.printNormal(0, "CARD #: XXXXXXXXXXXX6158\n");
            printer.printNormal(0, "AUTH CODE: 952823  SEQ: 029620\n");
            printer.printNormal(0, "Ticket #: 31962508244326161\n");
            printer.printNormal(0, "TransID: 185191625082443185\n");

            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));

            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/divider.png"));

            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_LARGE)+printer.getCommand(PrinterCommands.ALIGN_CENTER) + "3 Items\n");

            printer.printNormal(0, printer.getCommand(PrinterCommands.FONT_SIZE_MEDIUM));
            printer.printNormal(0, printer.getCommand(PrinterCommands.LINE_SPACING_SINGLE));
            printer.printNormal(0, printer.getCommand(PrinterCommands.FORMAT_NORMAL));
            printer.printNormal(0, printer.getCommand(PrinterCommands.ALIGN_LEFT));

            printer.printNormal(0, "\n");

            printer.printNormal(0,"Pals Rewards Number: 462411413\n");

            printer.printNormal(0,BOLD +"You saved 30.00 today.\n\n" + NORMAL);

            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Thank you for shopping at JumpMind!\n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Questions or comments?\n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Call us at 888-942-5867\n\n");

            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"JumpMind provides professional \n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"implementation and consulting services \n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"for all the software we offer. \n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"For more go to "+ BOLD + "jumpmind.com" + NORMAL + "\n\n");

            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"enterprise pos, mobile, customer facing,\n self-checkout, with complete retail grid: \n");

            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/marketing-image.png"));


            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"\nNu Commerce is the new robust, \nenterprise retail solution. \n\n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"Schedule a " + BOLD + "demo " + NORMAL + "today with\n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+"our sales team. Shoot us an email at  \n");
            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+ BOLD + "sales@jumpmind.com" + NORMAL + "\n\n");

            printer.printNormal(0,printer.getCommand(PrinterCommands.ALIGN_CENTER)+ BOLD + "Brought to you by" + NORMAL + "\n");

            printer.printImage(Thread.currentThread().getContextClassLoader().getResourceAsStream("images/jumpmind-logo.png"));

            printer.printNormal(0, "\n\n");

            printer.printBarCode(POSPrinterConst.PTR_S_RECEIPT,"380502001835720192324", POSPrinterConst.PTR_BCS_Code128, 50, 150,
                    POSPrinterConst.PTR_BC_CENTER, POSPrinterConst.PTR_BC_TEXT_BELOW);

            printer.printNormal(0, "\n\n\n\n\n\n");
            printer.cutPaper(100);

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
