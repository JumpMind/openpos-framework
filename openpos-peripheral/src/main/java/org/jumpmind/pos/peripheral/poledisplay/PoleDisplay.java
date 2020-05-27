package org.jumpmind.pos.peripheral.poledisplay;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.print.IConnectionFactory;
import org.jumpmind.pos.print.PeripheralConnection;
import org.jumpmind.pos.print.PrintException;
import org.jumpmind.pos.util.ClassUtils;
import org.jumpmind.pos.util.ReflectionException;
import org.jumpmind.pos.util.status.IStatusManager;
import org.jumpmind.pos.util.status.IStatusReporter;
import org.jumpmind.pos.util.status.Status;
import org.jumpmind.pos.util.status.StatusReport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class PoleDisplay implements IStatusReporter {

    final static String STATUS_NAME = "DEVICE.POLE_DISPLAY";

    final static byte CLEAR_DISPLAY = 0x0C; // NCR

    Map<String, Object> settings;
    PeripheralConnection peripheralConnection;
    IConnectionFactory connectionFactory;

    IStatusManager statusManager;

    public PoleDisplay() {

    }

    public void open(Map<String,Object> settings) {
        this.settings = settings;
        try {
            String className = (String)this.settings.get("connectionClass");
            if (StringUtils.isEmpty(className)) {
                throw new ReflectionException("The connectionClass setting is required for the pole display, but was not provided.");
            }
            this.connectionFactory = ClassUtils.instantiate(className);
        } catch (Exception ex) {
            if (statusManager != null) {
                statusManager.reportStatus(new StatusReport(STATUS_NAME, Status.Offline, ex.getMessage()));
            }
            throw new PrintException("Failed to create the connection factory for " + getClass().getName(), ex);
        }
        this.peripheralConnection = connectionFactory.open(this.settings);
    }

    public void close() {
        this.connectionFactory.close(peripheralConnection);
    }

    public void showText(String text) {
        try {
            if (peripheralConnection == null || peripheralConnection.getOut() == null) {
                log.warn("Not connected to pole display to show text: " + text);
                this.peripheralConnection = connectionFactory.open(this.settings);
            }
            peripheralConnection.getOut().write(CLEAR_DISPLAY);
            peripheralConnection.getOut().write(text.getBytes());
        } catch (Exception ex) {
            if (statusManager != null) {
                statusManager.reportStatus(new StatusReport(STATUS_NAME, Status.Error, ex.getMessage()));
            }
            log.warn("Failed to write text to the PoleDisplay \"" + text + "\"", ex);
        }
    }

    @Override
    public StatusReport getStatus(IStatusManager statusManager) {
        this.statusManager = statusManager;

        Status status = (peripheralConnection != null && peripheralConnection.getOut() != null)
                ? Status.Online : Status.Offline;

        StatusReport report = new StatusReport(STATUS_NAME, status);
        return report;
    }
}
