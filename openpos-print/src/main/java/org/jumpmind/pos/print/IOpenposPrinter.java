package org.jumpmind.pos.print;

import jpos.services.POSPrinterService19;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface IOpenposPrinter extends POSPrinterService19 {

    public void printImage(InputStream image);

    public void openCashDrawer(String cashDrawerId);

    public String getCommand(String fontSizeMedium);

    public int getPrintWidth();

    public void init(Map<String,Object> settings);

    public String getPrinterName();

    public PrinterConnection getPrinterConnection();

    public void printSlip(String text, int timeoutInMillis);
}
