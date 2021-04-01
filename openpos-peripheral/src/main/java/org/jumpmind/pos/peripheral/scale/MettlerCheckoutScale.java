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

import javax.annotation.PreDestroy;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class MettlerCheckoutScale extends CheckoutScale {

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

    final int MIN_RESPONSE_LENGTH = 3;

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

    public ScaleWeightData parseResponse(byte[] response) {
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
        return scaleWeightData;
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

    protected int getResponseLength() {
        return MIN_RESPONSE_LENGTH;
    }

    public static void main(String[] args) throws Exception {
        // 360018794
        MettlerCheckoutScale scale = new MettlerCheckoutScale();
        Map<String, Object> settings = new HashMap<>();

        settings.put("connectionClass", RS232JSerialCommConnectionFactory.class.getName());
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
