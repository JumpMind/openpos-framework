package org.jumpmind.pos.print;

import jpos.POSPrinterConst;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.net.URL;

public class PrinterCommandsTest {

    private PrinterCommands printerCommands;

    public PrinterCommandsTest() {
        printerCommands = new PrinterCommands();
        printerCommands.load(getProperties("esc_p.properties"));
        printerCommands.load(getProperties("epson.properties"));
    }

    @Test
    public void testLoadProperties() {
        String ESC = printerCommands.get(org.jumpmind.pos.print.PrinterCommands.ESC);
        assertEquals(String.valueOf((char)0x1B), ESC);

        String FORMAT_BOLD = printerCommands.get(org.jumpmind.pos.print.PrinterCommands.FORMAT_BOLD);

        assertEquals(new String(new char[] { 0x1B, 0x21, 0x8 } ), FORMAT_BOLD);
    }

    @Test
    public void testPrintProperties() {
        String ESC = printerCommands.get(org.jumpmind.pos.print.PrinterCommands.ESC);
        assertEquals(String.valueOf((char)0x1B), ESC);

        assertCommandsEqual(EscP_Constants.ALIGN_LEFT, printerCommands.get(PrinterCommands.ALIGN_LEFT));
        assertCommandsEqual(EscP_Constants.ALIGN_CENTER, printerCommands.get(PrinterCommands.ALIGN_CENTER));
        assertCommandsEqual(EscP_Constants.FORMAT_NORMAL, printerCommands.get(PrinterCommands.FORMAT_NORMAL));
        assertCommandsEqual(EscP_Constants.FORMAT_BOLD, printerCommands.get(PrinterCommands.FORMAT_BOLD));
        assertCommandsEqual(EscP_Constants.LINE_SPACING_SINGLE_EPSON, printerCommands.get(PrinterCommands.LINE_SPACING_SINGLE));
        assertCommandsEqual(EscP_Constants.LINE_SPACING_1_AND_HALF_EPSON, printerCommands.get(PrinterCommands.LINE_SPACING_1_AND_HALF));
        assertCommandsEqual(EscP_Constants.LINE_SPACING_TIGHT_EPSON, printerCommands.get(PrinterCommands.LINE_SPACING_TIGHT));
        assertCommandsEqual(EscP_Constants.FONT_SIZE_MEDIUM, printerCommands.get(PrinterCommands.FONT_SIZE_MEDIUM));
        assertCommandsEqual(EscP_Constants.FONT_SIZE_LARGE_EPSON, printerCommands.get(PrinterCommands.FONT_SIZE_LARGE));

        EscpPOSPrinter printer = new EscpPOSPrinter();
        printer.printerCommands = printerCommands;
        printer.connectionFactory = new ByteArrayConnectionFactory();
        printer.open("Printer", null);

        {
            String barcodeCommand = printer.buildBarcodeCommand(0, "38050200183572019232", POSPrinterConst.PTR_BCS_Code128, 0, 0, 0, 0);

            byte[] barcodeEvenDigits = new byte[] {
                    /* set text below barcode */ 0x1D, 0x48, 2,
                    /* set height=90 */ 0x1D, 0x68, 90,
                    /* set width=2 */0x1D, 0x77, 2,
                    /* select CODE128 */ 0x1D, 0x6B, 73,
                    /** width in byte length of barcode data */ 12,
                    /* select CODEC */ 123, 67,
                    /* barcode data */ 38, 5, 2, 0, 18, 35, 72, 1, 92, 32} ;

            assertCommandsEqual(new String(barcodeEvenDigits), barcodeCommand);
        }
        {
            String barcodeCommand = printer.buildBarcodeCommand(0, "380502001835720192324", POSPrinterConst.PTR_BCS_Code128, 0, 0, 0, 0);

            byte[] barcodeOddDigits = new byte[] {
                    /* set text below barcode */ 0x1D, 0x48, 2,
                    /* set height=90 */ 0x1D, 0x68, 90,
                    /* set width=2 */0x1D, 0x77, 2,
                    /* select CODE128 */ 0x1D, 0x6B, 73,
                    /** width in byte length of barcode data */ 15,
                    /* select CODEC */ 123, 67,
                    /* barcode data */ 38, 5, 2, 0, 18, 35, 72, 1, 92, 32,
                    /* select CODEB for the last, odd digit*/ 123, 66,
                    /* barcode data */ (char)'4'
            } ;

            assertCommandsEqual(new String(barcodeOddDigits), barcodeCommand);
        }
        {
            String barcodeCommand = printer.buildBarcodeCommand(0, "ABC123", POSPrinterConst.PTR_BCS_Code128, 0, 0, 0, 0);

            byte[] barcodeAlphaNumeric = new byte[] {
                    /* set text below barcode */ 0x1D, 0x48, 2,
                    /* set height=90 */ 0x1D, 0x68, 90,
                    /* set width=2 */0x1D, 0x77, 2,
                    /* select CODE128 */ 0x1D, 0x6B, 73,
                    /** width in byte length of barcode data */ 8,
                    /* select CODEB */ 123, 66,
                    /* barcode data */ 'A', 'B', 'C', '1', '2', '3',
            } ;

            assertCommandsEqual(new String(barcodeAlphaNumeric), barcodeCommand);
        }

    }

    private void assertCommandsEqual(String expected, String actual) {
        assertEquals("\nexpected: " + org.jumpmind.pos.print.PrinterCommands.stringToHexString(expected) +
                "       !=\nactual:   " + org.jumpmind.pos.print.PrinterCommands.stringToHexString(actual), expected, actual);
    }

    public URL getProperties(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }
}
