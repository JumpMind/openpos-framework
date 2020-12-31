package org.jumpmind.pos.peripheral.scale;

import org.jumpmind.pos.peripheral.PeripheralException;

import java.math.BigDecimal;

public class DetectoCheckoutScale extends CheckoutScale {

    final byte SCALE_AT_ZERO = 112;

    protected void performConfidenceTest() {
        // not available.
    }

    @Override
    protected ScaleWeightData parseErrorResponse(byte[] response) {
        String statusMessage = "";
        byte statusByte = response[2];

        if (statusByte == SCALE_AT_ZERO) {
            ScaleWeightData scaleWeightData = new ScaleWeightData();
            scaleWeightData.setSuccessful(true);
            scaleWeightData.setWeight(BigDecimal.ZERO);
            return scaleWeightData;
        }

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
}
