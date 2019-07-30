package org.jumpmind.pos.print;

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
        assertCommandsEqual(EscP_Constants.ESP_P_MODE, printerCommands.get(PrinterCommands.ESC_P_MODE));
        assertCommandsEqual(EscP_Constants.LINE_SPACING_SINGLE_EPSON, printerCommands.get(PrinterCommands.LINE_SPACING_SINGLE));
        assertCommandsEqual(EscP_Constants.LINE_SPACING_1_AND_HALF_EPSON, printerCommands.get(PrinterCommands.LINE_SPACING_1_AND_HALF));
        assertCommandsEqual(EscP_Constants.LINE_SPACING_TIGHT_EPSON, printerCommands.get(PrinterCommands.LINE_SPACING_TIGHT));
        assertCommandsEqual(EscP_Constants.FONT_SIZE_MEDIUM, printerCommands.get(PrinterCommands.FONT_SIZE_MEDIUM));
        assertCommandsEqual(EscP_Constants.FONT_SIZE_LARGE_EPSON, printerCommands.get(PrinterCommands.FONT_SIZE_LARGE));

        EscpPOSPrinter printer = new EscpPOSPrinter();
        printer.setPrinterCommands(printerCommands);
        printer.setOutputStream(new ByteArrayOutputStream());
        printer.open("Printer", null);

        String barcodeCommand = printer.buildBarcodeCommand(0, "380502001835720192324", 0, 0, 0, 0, 0);

        assertCommandsEqual(EscP_Constants.EPSON_CODE128_BARCODE, barcodeCommand);
    }

    private void assertCommandsEqual(String expected, String actual) {
        assertEquals(org.jumpmind.pos.print.PrinterCommands.stringToHexString(expected) +
                " != " + org.jumpmind.pos.print.PrinterCommands.stringToHexString(actual), expected, actual);
    }

    public URL getProperties(String name) {
        return Thread.currentThread().getContextClassLoader().getResource(name);
    }
}
