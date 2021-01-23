package org.jumpmind.pos.peripheral.scale;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.peripheral.IPlugAndPlayDevice;
import org.jumpmind.pos.peripheral.PeripheralException;
import org.jumpmind.pos.print.RS232JSerialCommConnectionFactory;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.pos.util.ClassUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DetectoAPS10CheckoutScale extends DetectoCheckoutScale {

    private final static int STATUS_POSITION = 8;
    private final static int WEIGHT_STRING_LENGTH = "wwww.ww".length();
    private final static byte WEIGHT_COMMAND = '~';
    private final static int DETECTO_RESPONSE_LENGTH = 12;

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

}
