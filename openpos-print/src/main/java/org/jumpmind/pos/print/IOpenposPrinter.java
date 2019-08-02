package org.jumpmind.pos.print;

import jpos.services.POSPrinterService19;

import java.io.InputStream;
import java.io.OutputStream;

public interface IOpenposPrinter extends POSPrinterService19 {

    public PrinterCommands getPrinterCommands();

    public void setPrinterCommands(PrinterCommands printerCommands);

    public void setOutputStream(OutputStream outputStream);

    public void printImage(InputStream image);

    public String getCommand(String fontSizeMedium);

    public int getPrintWidth();
}
