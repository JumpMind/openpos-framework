package org.jumpmind.pos.print;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

public class UsbConnectionFactory implements IConnectionFactory {

    private UsbConnection usbConnection;
    private ByteArrayOutputStream stream;
    private Map<String, Object> settings;

    @Override
    public OutputStream open(Map<String, java.lang.Object> settings) {
        this.settings = settings;
        UsbHelper usbHelper = new UsbHelper();

        short vendorId = getId(settings, "usbVendorId");
        short deviceId = getId(settings, "usbDeviceId");

        try {
            usbConnection = usbHelper.openUsbConnection(vendorId, deviceId);
;        } catch (Exception ex) {
            throw new PrintException("Failed to open USB connection to printer using settings: " + settings, ex);
        }

        stream = new ByteArrayOutputStream();

        return stream;
    }

    @Override
    public void close() {
        if (usbConnection != null) {
            try {
                usbConnection.getUsbPipe().syncSubmit(stream.toByteArray());
                usbConnection.close();
            } catch (Exception ex) {
                throw new PrintException("Failed to write bytes to USB port: " + settings, ex);
            }
        }
    }

    protected short getId(Map<String, Object> settings, String key) {
        Object value = settings.get(key);
        if (value instanceof String && ((String) value).equalsIgnoreCase("ANY")
            || value == null) {
            return -1;
        } else if (value instanceof Integer) {
            return ((Integer)value).shortValue();
        } else {
            throw new PrintException("Don't know how to handle setting type for key: " + key +
                    ". Should be \"ANY\" or a hex id. Actual value was: " + value);
        }
    }

}
