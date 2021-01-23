package org.jumpmind.pos.peripheral.scale;

import gnu.io.SerialPort;
import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.peripheral.IPlugAndPlayDevice;
import org.jumpmind.pos.peripheral.PeripheralException;
import org.jumpmind.pos.print.RS232JSerialCommConnectionFactory;
import org.jumpmind.pos.util.ClassUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DetectoCheckoutScale extends CheckoutScale implements IPlugAndPlayDevice {

    // Detecto AS-334D response format:
    //
    // Pounds Only Mode:
    // AS-330D / AS-334D: swwwww.wwxccE
    // Where: s = weight sign (space or -)
    // wwwww.ww = weight - AS-330D / AS-334D (pounds and 2 position
    // decimal weight including the decimal point)
    // including the decimal point)
    // x = status: M = motion on scale
    //     C = scale over capacity
    // cc = checksum - these two characters are the ASCII
    // representation of the computed checksum
    //         E = ASCII ETX (HEX 03)

    final static int MIN_RESPONSE_LEGNTH = 10;
    final static int SIGN_POSITION = 0;

    // differs between model
    private final static int DETECTO_RESPONSE_LENGTH = 13;
    private final static int STATUS_POSITION = 9;
    private final static int WEIGHT_STRING_LENGTH = "wwwww.ww".length();
    private final static byte WEIGHT_COMMAND = 'W';

    public byte getWeightCommand() {
        return WEIGHT_COMMAND;
    }
    protected int getWeightStringLength() {
        return WEIGHT_STRING_LENGTH;
    }
    protected int getStatusPosition() {
        return STATUS_POSITION;
    }
    protected int getResponseLength() {
        return DETECTO_RESPONSE_LENGTH;
    }

    public ScaleWeightData parseResponse(byte[] response) {
        if (response == null || response.length < MIN_RESPONSE_LEGNTH) {
            log.warn("Response from scale too short: " + Arrays.toString(response));
            return createFailure(ScaleWeightData.CheckoutScaleFailureCode.UNSPECIFIED,
                    "Unable to communicate with the scale.");
        }

        char signChar = (char) response[SIGN_POSITION];
        if (signChar == '-') {
            return createFailure(ScaleWeightData.CheckoutScaleFailureCode.SCALE_READ_UNDER_0,
                    "Scale reading under 0.");
        }

        char statusChar = (char)response[getStatusPosition()];
        if (statusChar == 'M') {
            return createFailure(ScaleWeightData.CheckoutScaleFailureCode.SCALE_IN_MOTION,
                    "The scale is in motion");
        } else if (statusChar == 'C') {
            return createFailure(ScaleWeightData.CheckoutScaleFailureCode.SCALE_OVER_CAPACITY,
                    "The scale is over capacity");
        }

        StringBuilder weightString = new StringBuilder();
        for (int i = 1; i < getWeightStringLength()+1; i++) {
            weightString.append((char)response[i]);
        }

        BigDecimal weight;
        try {
            weight = new BigDecimal(weightString.toString().trim());
        } catch (Exception ex) {
            throw new PeripheralException("failed to convert scale weight to decimal: '" + weightString +
                    "' response: " + Arrays.toString(response), ex);
        }

        ScaleWeightData scaleWeightData = new ScaleWeightData();
        scaleWeightData.setWeight(weight);
        scaleWeightData.setSuccessful(true);
        return scaleWeightData;
    }

    @Override
    public boolean scanDevice(Map<String, Object> settings) {
        String className = this.getClass().getSimpleName();

        log.debug("Detecting {}...", className);
        try {
            String connectionClassName = (String) settings.get("connectionClass");
            this.connectionFactory = ClassUtils.instantiate(connectionClassName);
            this.peripheralConnection = connectionFactory.open(settings);
            byte[] bytes = sendScaleCommand(getWeightCommand());
            if (bytes.length == getResponseLength()) {
                log.info(className + " detected on " + settings.get("portName"));
                return true;
            }
        } catch (Exception ex) {
            this.peripheralConnection = null;
            log.info("Did not detect {}...", className);
            log.debug("Exception while detecting " + className, ex);
        } finally {
            close();
        }

        return false;
    }

    public static void main(String[] args) throws Exception {
        // 360018794
        DetectoCheckoutScale scale = new DetectoCheckoutScale();
        Map<String, Object> settings = new HashMap<>();

        settings.put("connectionClass", RS232JSerialCommConnectionFactory.class.getName());
        settings.put(RS232JSerialCommConnectionFactory.BAUD_RATE, 9600);
        settings.put("portName", "/dev/tty.usbserial-1420");


        scale.scanDevice(settings);

        scale.initialize(settings);

        int tries = 200;
        while (tries-- > 0) {

            ScaleWeightData weightData = scale.getScaleWeightData();
            if (weightData.isSuccessful()) {
                System.out.println("Read weight: " + weightData.getWeight());
            } else {
                System.out.println("Failed: " + weightData.getFailureMessage());
            }
        }
    }
}
