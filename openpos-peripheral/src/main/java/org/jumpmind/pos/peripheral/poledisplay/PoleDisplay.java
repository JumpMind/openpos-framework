package org.jumpmind.pos.peripheral.poledisplay;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.peripheral.PeripheralException;
import org.jumpmind.pos.print.IConnectionFactory;
import org.jumpmind.pos.print.PeripheralConnection;
import org.jumpmind.pos.util.ClassUtils;
import org.jumpmind.pos.util.ReflectionException;
import org.jumpmind.pos.util.status.IStatusManager;
import org.jumpmind.pos.util.status.IStatusReporter;
import org.jumpmind.pos.util.status.Status;
import org.jumpmind.pos.util.status.StatusReport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.Map;

@Slf4j
@Component
@ConditionalOnProperty(name = "openpos.devices.poleDisplay.enabled", havingValue = "true")
public class PoleDisplay implements IStatusReporter {

    public final static String STATUS_NAME = "DEVICE.POLE_DISPLAY";
    public final static String STATUS_ICON = "pole-display";

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
                statusManager.reportStatus(new StatusReport(STATUS_NAME, STATUS_ICON, Status.Offline, ex.getMessage()));
            }
            throw new PeripheralException("Failed to create the connection factory for " + getClass().getName(), ex);
        }
        log.info("Opening pole display with settings: " + this.settings);
        this.peripheralConnection = connectionFactory.open(this.settings);
        showText("Starting");
        log.info("Pole display appears to be successfully opened.");
    }

    public void showText(String text) {
        try {
            if (peripheralConnection == null || peripheralConnection.getOut() == null) {
                log.warn("Not connected to pole display to show text: " + text);
                this.peripheralConnection = connectionFactory.open(this.settings);
            }
            if (log.isDebugEnabled()) {
                log.debug("Displaying to pole display: '" + text + "'");
            }
            peripheralConnection.getOut().write(CLEAR_DISPLAY);
            peripheralConnection.getOut().write(text.getBytes());
            if (statusManager != null) {
                statusManager.reportStatus(new StatusReport(STATUS_NAME, STATUS_ICON, Status.Online));
            }
        } catch (Exception ex) {
            if (statusManager != null) {
                statusManager.reportStatus(new StatusReport(STATUS_NAME, STATUS_ICON, Status.Error, ex.getMessage()));
            }
            log.warn("Failed to write text to the PoleDisplay \"" + text + "\"", ex);
        }
    }

    @Override
    public StatusReport getStatus(IStatusManager statusManager) {
        this.statusManager = statusManager;

        Status status = (peripheralConnection != null && peripheralConnection.getOut() != null)
                ? Status.Online : Status.Offline;

        if (this.settings == null) {
            status = Status.Disabled;
        }

        StatusReport report = new StatusReport(STATUS_NAME, STATUS_ICON, status);
        return report;
    }

    @PreDestroy
    public void close() {
        if (this.peripheralConnection != null) {
            try {
                log.info("Closing connection to the pole display.");
                connectionFactory.close(peripheralConnection);
                peripheralConnection = null;
                log.info("Pole display appears to be successfully closed.");
            } catch (Exception ex) {
                log.warn("Failed to cleanly close connection to the pole display.", ex);
            }
        }
    }
}
