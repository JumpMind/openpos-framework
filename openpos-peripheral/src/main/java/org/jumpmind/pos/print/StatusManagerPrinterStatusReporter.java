package org.jumpmind.pos.print;

import org.jumpmind.pos.util.status.IStatusManager;
import org.jumpmind.pos.util.status.IStatusReporter;
import org.jumpmind.pos.util.status.Status;
import org.jumpmind.pos.util.status.StatusReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "openpos.devices.enableMobilePrint", havingValue = "false")
public class StatusManagerPrinterStatusReporter implements IStatusReporter, IPrinterStatusReporter {
    private IStatusManager statusManager;

    private StatusReport lastStatus;

    public static final String STATUS_NAME = "DEVICE.RECEIPT_PRINTER";
    public static final String STATUS_ICON = "print";

    @Override
    public StatusReport getStatus(IStatusManager statusManager) {
        // in the future, this could call EscPosPrinter.getPrinterStatus() for a live status
        // diagnostic from the printer.
        this.statusManager = statusManager;
        if (lastStatus == null) {

            return new StatusReport(STATUS_NAME, STATUS_ICON, Status.Unknown, "");
        } else {
            return lastStatus;
        }
    }

    public void reportStatus(Status status, String message) {
        this.lastStatus = new StatusReport(STATUS_NAME, STATUS_ICON, status, message);
        if (statusManager != null) {
            statusManager.reportStatus(lastStatus);
        }
    }
}
