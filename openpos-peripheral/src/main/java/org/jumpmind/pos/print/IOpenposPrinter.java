package org.jumpmind.pos.print;

import jpos.services.POSPrinterService19;

import java.io.InputStream;
import java.util.Map;

public interface IOpenposPrinter extends POSPrinterService19 {

    static final int DRAWER_OPEN = 0;
    static final int DRAWER_CLOSED = 1;

    public void printImage(InputStream image);

    public default boolean supportsPrintDivider() {
        return false;
    }

    public default void printDivider() {

    }

    public void openCashDrawer(String cashDrawerId);

    public String getCommand(String commandName);

    public int getPrintWidth();

    public void init(Map<String,Object> settings, IPrinterStatusReporter printerStatusReporter);

    public String getPrinterName();

    public PeripheralConnection getPeripheralConnection();

    int readPrinterStatus();

    public boolean isDrawerOpen(String cashDrawerId);

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
