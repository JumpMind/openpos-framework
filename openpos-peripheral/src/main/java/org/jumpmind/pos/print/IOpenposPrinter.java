package org.jumpmind.pos.print;

import jpos.services.POSPrinterService19;

import java.io.InputStream;
import java.util.Map;

public interface IOpenposPrinter extends POSPrinterService19 {

    public void printImage(InputStream image);

    public void openCashDrawer(String cashDrawerId);

    public String getDrawerStatus(String cashDrawerId);

    public String getCommand(String commandName);

    public int getPrintWidth();

    public void init(Map<String,Object> settings, PrinterStatusReporter printerStatusReporter);

    public String getPrinterName();

    public PeripheralConnection getPeripheralConnection();

    int readPrinterStatus();

    public int waitForDrawerClose(String cashDrawerId, long timeout);

    public void beginSlipMode();

    public void endSlipMode();

    public void printSlip(String text, int timeoutInMillis);

    /**
     * this execpts the slip to be in the printer at the point this is called.
     * Use !getJrnlEmpty() to determine when the slip is in place.
     * @return raw MICR string as read from the document.
     */
    public String readMicr();
}
