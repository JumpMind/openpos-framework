package org.jumpmind.pos.peripheral.scale;

import lombok.Getter;
import lombok.Setter;
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

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Setter
public abstract class CheckoutScale implements IStatusReporter, ICheckoutScale {

    public final static String STATUS_NAME = "DEVICE.CHECKOUT_SCALE";
    public final static String STATUS_ICON = "checkout_scale";

    Map<String, Object> settings;
    PeripheralConnection peripheralConnection;
    IConnectionFactory connectionFactory;

    private String scaleUnit;
    private long pollInterval;
    private long readTimeout;

    IStatusManager statusManager;
    private Status lastStatus = Status.Unknown;

    public void initialize(Map<String,Object> settings) {
        this.settings = settings;
        open(settings);
        close();
    }

    void open(Map<String,Object> settings) {
        if (this.peripheralConnection != null) {
            throw new PeripheralException("peripheralConnection should be null when open is called. Scale might already be open.");
        }

        readTimeout = getInt(settings,"readTimeout", 20*1000);
        pollInterval = getInt(settings,"pollInterval", 300);
        scaleUnit = (String)settings.get("scaleUnit");

        try {
            String className = (String) this.settings.get("connectionClass");
            if (StringUtils.isEmpty(className)) {
                throw new ReflectionException("The connectionClass setting is required for the checkout scale, but was not provided.");
            }
            this.connectionFactory = ClassUtils.instantiate(className);
        } catch (Exception ex) {
            lastStatus = Status.Offline;
            if (statusManager != null) {
                statusManager.reportStatus(new StatusReport(STATUS_NAME, STATUS_ICON, lastStatus, ex.getMessage()));
            }
            throw new PeripheralException("Failed to create the connection factory for " + getClass().getName(), ex);
        }
        try {
            log.info("Opening checkout scale with settings: " + this.settings);
            this.peripheralConnection = connectionFactory.open(this.settings);
            log.info("Checkout scale appears to be successfully opened.");
        } catch (Exception ex) {
            this.peripheralConnection = null;
            lastStatus = Status.Offline;
            if (statusManager != null) {
                statusManager.reportStatus(new StatusReport(STATUS_NAME, STATUS_ICON, lastStatus, ex.getMessage()));
            }
            throw new PeripheralException("Failed to open connection to the checkout scale.", ex);
        }
    }


        @PreDestroy
    public void close() {
        if (this.peripheralConnection != null) {
            try {
                log.info("Closing connection to checkout scale.");
                connectionFactory.close(peripheralConnection);
                this.peripheralConnection = null;
            } catch (Exception ex) {
                log.warn("Failed to cleanly close connection to checkout scale.", ex);
            }
        }
    }

    protected byte[] sendScaleCommand(byte command) {
        try {
            if (this.peripheralConnection == null) {
                throw new PeripheralException("The checkout scale is not open.");
            }
            this.peripheralConnection.getOut().write(command);
            this.peripheralConnection.getOut().flush();
            try {
                Thread.sleep(300);
            } catch (Exception ex) {
                log.debug("sendScaleCommand interruppted", ex);
            }

            List<Integer> bytes = new ArrayList<Integer>();

            int responseByte = -1;

            while (this.peripheralConnection.getIn().available() > 0) {
                responseByte = this.peripheralConnection.getIn().read();
                if (responseByte != '\r' && responseByte != '\n') { // most scale commands terminate with \r\n
                    bytes.add(responseByte);
                }
            }

            byte[] response = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                response[i] = bytes.get(i).byteValue();
            }

            if (response.length == 0 ) {
                throw new PeripheralException("Unrecognized response from checkout scale. "
                        + Arrays.toString(response));
            }

            return response;

        } catch (Exception ex) {
            if (ex instanceof PeripheralException) {
                throw (PeripheralException)ex;
            }
            throw new PeripheralException("Failed to send scale command " + command, ex);
        }
    }

    @Override
    public StatusReport getStatus(IStatusManager statusManager) {
        this.statusManager = statusManager;

        Status status = getLastStatus();

        if (this.settings == null) {
            status = Status.Disabled;
        }

        StatusReport report = new StatusReport(STATUS_NAME, STATUS_ICON, status);
        return report;
    }

    public synchronized ScaleWeightData getScaleWeightData() {
        if (this.settings != null) {
            open(this.settings);
        }

        try {
            byte[] response = sendScaleCommand((byte)'W');
            if (response.length < 2) {
                throw new PeripheralException("Unrecognized response from checkout scale " + Arrays.toString(response));
            }

            return parseResponse(response);
        } catch (Exception ex) {
            setLastStatus(Status.Error);
            if (statusManager != null) {
                statusManager.reportStatus(new StatusReport(STATUS_NAME, STATUS_ICON, Status.Error, ex.getMessage()));
            }
            if (ex instanceof PeripheralException) {
                throw (PeripheralException)ex;
            } else {
                throw new PeripheralException("Failed to read weight from scale.", ex);
            }
        } finally {
            close();
        }
    }

    public abstract ScaleWeightData parseResponse(byte[] response);

    private int getInt(Map<String, Object> settings, String key, int defaultValue) {
        if (settings.containsKey(key)) {
            return Integer.valueOf(settings.get(key).toString());
        } else {
            return defaultValue;
        }
    }

}
