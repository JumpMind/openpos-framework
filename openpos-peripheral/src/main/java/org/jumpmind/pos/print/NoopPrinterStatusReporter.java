package org.jumpmind.pos.print;

import org.jumpmind.pos.util.status.Status;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "openpos.devices.enableMobilePrint", havingValue = "true")
public class NoopPrinterStatusReporter implements IPrinterStatusReporter {
    @Override
    public void reportStatus(Status status, String message) {}
}
