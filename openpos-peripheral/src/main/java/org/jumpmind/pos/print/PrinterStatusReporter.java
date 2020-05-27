package org.jumpmind.pos.print;

import org.jumpmind.pos.util.status.IStatusManager;
import org.jumpmind.pos.util.status.IStatusReporter;
import org.jumpmind.pos.util.status.Status;
import org.jumpmind.pos.util.status.StatusReport;
import org.springframework.stereotype.Component;

@Component
public class PrinterStatusReporter implements IStatusReporter {

    private IStatusManager statusManager;

    private StatusReport lastStatus;

    public static final String STATUS_NAME = "DEVICE.RECEIPT_PRINTER";

    @Override
    public StatusReport getStatus(IStatusManager statusManager) {
        // in the future, this could call EscPosPrinter.getPrinterStatus() for a live status
        // diagnostic from the printer.
        this.statusManager = statusManager;
        return lastStatus;
    }

    public void reportStatus(Status status, String message) {
        this.lastStatus = new StatusReport(STATUS_NAME, status, message);
        if (statusManager != null) {
            statusManager.reportStatus(lastStatus);
        }
    }
}
