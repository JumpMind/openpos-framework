package org.jumpmind.pos.print;

import org.jumpmind.pos.util.status.Status;

public interface IPrinterStatusReporter {
    void reportStatus(Status status, String message);
}
