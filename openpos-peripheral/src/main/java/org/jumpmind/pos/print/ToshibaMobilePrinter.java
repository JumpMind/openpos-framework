package org.jumpmind.pos.print;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.pos.util.status.Status;

import java.io.InputStream;

@Slf4j
public class ToshibaMobilePrinter extends EscpPOSPrinter {

    @Override
    public void printImage(InputStream image) {
        // images not supported yet 5/13/2021. Protocol should be the same as the base implementation,
        // but they are not working over Mac bluetooth connection. Should test on windows next.
    }

    protected void initializePrinter() {
    }

    @Override
    public int readPrinterStatus() {
        try {
            getPeripheralConnection().getOut().flush();
            getPeripheralConnection().getOut().write('\n');
            return 0;
        } catch (Exception ex) {
            if (printerStatusReporter != null) {
                printerStatusReporter.reportStatus(Status.Error, ex.getMessage());
            }
            throw new PrintException("readPrinterStatus() failed ", ex);
        }
    }

    @Override
    public boolean supportsPrintDivider() {
        return true;
    }

    @Override
    public void printDivider() {
        printNormal(0, StringUtils.repeat('_', getPrintWidth()) + "\n");
    }

    @Override
    public void cutPaper(int percentage) {
        printNormal(0, "\n\n\n\n\n");  // epson will cut through barcode without some feed
        attemptToWorkaroundFlushingProblem();
    }

    private void attemptToWorkaroundFlushingProblem() {
        try {
            AppUtils.sleep(1000);
            close();
            AppUtils.sleep(1000);
            open(this.getPrinterName(), null);
        } catch (Exception ex) {
            log.warn("Failed to cleanly re-init printer.", ex);
        }
    }

    @Override
    public void printBarCode(int station, String data, int symbology, int height, int width, int alignment, int textPosition) {
        try {
            String printBarcodeCommand = buildBarcodeCommand(station, data, symbology, height, width, alignment, textPosition);
            printNormal(0, printBarcodeCommand);
        } catch (Exception ex) {
            throw new PrintException("Failed to print barcode: " + data + " symbology: " + symbology, ex);
        }
    }

}
