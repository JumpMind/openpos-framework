package org.jumpmind.pos.peripheral.scale;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.peripheral.PeripheralException;
import org.jumpmind.pos.print.*;
import org.jumpmind.pos.util.ClassUtils;
import org.jumpmind.pos.util.ReflectionException;
import org.jumpmind.pos.util.status.IStatusManager;
import org.jumpmind.pos.util.status.IStatusReporter;
import org.jumpmind.pos.util.status.Status;
import org.jumpmind.pos.util.status.StatusReport;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class CheckoutScale implements IStatusReporter, ICheckoutScale {

    public final static String STATUS_NAME = "DEVICE.CHECKOUT_SCALE";
    public final static String STATUS_ICON = "checkout_scale";

    static final int SCALE_RESPONSE_BYTE = 0b0000010;
    static final int SCALE_ERROR_BYTE = '?';

    static final int STATUS_SCALE_IN_MOTION             = 0b0000001;
    static final int STATUS_OVER_CAPACITY               = 0b0000010;
    static final int STATUS_UNDER_0                     = 0b0000100;
    static final int STATUS_OUTSIDE_ZERO_CAPTURE_RANGE  = 0b0001000;
    static final int STATUS_CENTER_OF_0                 = 0b0010000;
    static final int STATUS_NET_WEIGHT                  = 0b0100000;
    static final int STATUS_BAD_COMMAND                 = 0b1000000;

    final static char TARE_CHARACTER = 'N';

    Map<String, Object> settings;
    PeripheralConnection peripheralConnection;
    IConnectionFactory connectionFactory;

    IStatusManager statusManager;
    private Status lastStatus = Status.Unknown;

    public void initialize(Map<String,Object> settings) {
        this.settings = settings;
        open(settings, true);
        close();
    }

    void open(Map<String,Object> settings, boolean runConfidenceTest) {
        if (this.peripheralConnection != null) {
            throw new PeripheralException("peripheralConnection should be null when open is called. Scale might already be open.");
        }
        try {
            String className = (String)this.settings.get("connectionClass");
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

            if (runConfidenceTest) {
                performConfidenceTest();
            }
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

    public synchronized ScaleWeightData getScaleWeightData() {
        if (this.settings != null) {
            open(this.settings, false);
        }

        try {
            byte[] response = sendScaleCommand((byte)'W');
            if (response.length < 2) {
                throw new PeripheralException("Unrecognized response from checkout scale " + Arrays.toString(response));
            }

            return parseResponse(response);
        } catch (Exception ex) {
            lastStatus = Status.Error;
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

    protected ScaleWeightData parseResponse(byte[] response) {
        if (response[1] == SCALE_ERROR_BYTE) {
            return parseErrorResponse(response);
        }
        StringBuilder weightString = new StringBuilder();
        for (int i = 1; i < response.length; i++) {
            weightString.append((char)response[i]);
        }

        BigDecimal weight;
        try {
            if (weightString.charAt(weightString.length()-1) == TARE_CHARACTER) { // not sure how to act on this tare character yet.
                weightString.setLength(weightString.length()-1);
            }
            weight = new BigDecimal(weightString.toString());
        } catch (Exception ex) {
            throw new PeripheralException("failed to convert scale weight to decimal: '" + weightString + "'", ex);
        }

        ScaleWeightData scaleWeightData = new ScaleWeightData();
        scaleWeightData.setWeight(weight);
        scaleWeightData.setSuccessful(true);
        lastStatus = Status.Online;
        if (statusManager != null) {
            statusManager.reportStatus(new StatusReport(STATUS_NAME, STATUS_ICON, Status.Online));
        }
        return scaleWeightData;

    }

    protected ScaleWeightData parseErrorResponse(byte[] response) {
        String statusMessage = "";
        byte statusByte = response[2];
        if (statusByte > 0) {
            ScaleWeightData scaleWeightData = new ScaleWeightData();
            scaleWeightData.setSuccessful(false);
            statusMessage = checkStatus(scaleWeightData, statusByte);
            if (scaleWeightData.getFailureCode() != ScaleWeightData.CheckoutScaleFailureCode.UNSPECIFIED) {
                scaleWeightData.setFailureMessage(statusMessage);
                return scaleWeightData;
            }
        } else {
            performConfidenceTest();
        }

        throw new PeripheralException("Failed to read checkout scale weight. " + statusMessage);
    }

    protected void performConfidenceTest() {
        sendScaleCommand((byte)'A');
        byte[] response = sendScaleCommand((byte)'B');

        if (response.length < 3) {
            throw new PeripheralException("Unrecognized confidence response from checkout scale " + Arrays.toString(response));
        }

        final int STATUS_BYTE_POSITION = 2;

        byte statusByte = response[STATUS_BYTE_POSITION];
        if (statusByte == '@') {
            log.info("Successful checkout scale confidence test.");
            return;
        } else {
            throw new PeripheralException("Failed checkout scale confidence test with status " + statusByte);
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
                Thread.sleep(200);
            } catch (Exception ex) {
                log.debug("sendScaleCommand interruppted", ex);
            }

            List<Integer> bytes = new ArrayList<Integer>();

            int responseByte = -1;

            while ((responseByte = this.peripheralConnection.getIn().read()) != -1) {
                if (responseByte != '\r' && responseByte != '\n') { // most scale commands terminate with \r\n
                    bytes.add(responseByte);
                }
            }

            byte[] response = new byte[bytes.size()];
            for (int i = 0; i < bytes.size(); i++) {
                response[i] = bytes.get(i).byteValue();
            }

            if (response.length == 0 || response[0] != SCALE_RESPONSE_BYTE) {
                throw new PeripheralException("Unrecognized response from checkout scale. Should start with byte " +
                        SCALE_RESPONSE_BYTE + " but was " + Arrays.toString(response));
            }

            return response;

        } catch (Exception ex) {
            if (ex instanceof PeripheralException) {
                throw (PeripheralException)ex;
            }
            throw new PeripheralException("Failed to send scale command " + command, ex);
        }
    }

    protected String checkStatus(ScaleWeightData scaleWeightData, int statusByte) {
        StringBuilder buff = new StringBuilder();

        if ((statusByte & STATUS_NET_WEIGHT) > 0) {
            buff.append("(net weight)");
        }
        if ((statusByte & STATUS_SCALE_IN_MOTION) > 0) {
            buff.append("The scale is in motion.");
            scaleWeightData.setFailureCode(ScaleWeightData.CheckoutScaleFailureCode.SCALE_IN_MOTION);
        }
        if ((statusByte & STATUS_OVER_CAPACITY) > 0) {
            buff.append("The scale is over capacity");
            scaleWeightData.setFailureCode(ScaleWeightData.CheckoutScaleFailureCode.SCALE_OVER_CAPACITY);
        }
        if ((statusByte & STATUS_UNDER_0) > 0) {
            buff.append("The scale is reading under 0");
            scaleWeightData.setFailureCode(ScaleWeightData.CheckoutScaleFailureCode.SCALE_READ_UNDER_0);
        }
        if ((statusByte & STATUS_OUTSIDE_ZERO_CAPTURE_RANGE) > 0) {
            buff.append("The scale is outside zero capture range");
        }
        if ((statusByte & STATUS_CENTER_OF_0) > 0) {
            buff.append("The scale center of 0");
        }

        buff.append(" 0b").append(Integer.toBinaryString(statusByte));

        return buff.toString();
    }

    @Override
    public StatusReport getStatus(IStatusManager statusManager) {
        this.statusManager = statusManager;

        Status status = lastStatus;

        if (this.settings == null) {
            status = Status.Disabled;
        }

        StatusReport report = new StatusReport(STATUS_NAME, STATUS_ICON, status);
        return report;
    }

    public static void main(String[] args) throws Exception {
        // 360018794
        CheckoutScale scale = new DetectoCheckoutScale();
        Map<String, Object> settings = new HashMap<>();

        settings.put("connectionClass", RS232ConnectionFactory.class.getName());
        settings.put("portName", "COM7");
        scale.initialize(settings);

        int tries = 200;
        while (tries-- > 0) {
            System.out.println("Waiting...");
            Thread.sleep(5000);
            ScaleWeightData weightData = scale.getScaleWeightData();
            if (weightData.isSuccessful()) {
                System.out.println("Read weight: " + weightData.getWeight());
            } else {
                System.out.println("Failed: " + weightData.getFailureMessage());
            }
        }



    }
            }
