package org.jumpmind.pos.print;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.peripheral.PeripheralException;
import org.jumpmind.pos.util.MutableByteArrayInputStream;

import javax.usb.UsbConst;
import javax.usb.UsbEndpoint;
import javax.usb.UsbIrp;
import javax.usb.UsbPipe;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
public class UsbConnectionFactory implements IConnectionFactory {

    private ByteArrayOutputStream outputStream;
    private Map<String, Object> settings;

    @Override
    public PeripheralConnection open(Map<String, java.lang.Object> settings) {
        this.settings = settings;
        UsbHelper usbHelper = new UsbHelper();

        short vendorId = getId(settings, "usbVendorId");
        short deviceId = getId(settings, "usbDeviceId");

        final UsbConnection usbConnection;

        try {
            usbConnection = usbHelper.openUsbConnection(vendorId, deviceId);
        } catch (Exception ex) {
            if (ex instanceof PeripheralException) {
                throw ex;
            }
            throw new PeripheralException("Failed to open USB connection to device using settings: " + settings, ex);
        }

        outputStream = new ByteArrayOutputStream() {
            @Override
            public void flush() throws IOException {
                super.flush();
                try {
                    byte[] bytes = toByteArray();
                    reset();
                    int bytesSent = usbConnection.getUsbPipe().syncSubmit(bytes);
                } catch (Exception ex) {
                    throw new PeripheralException("Failed to write bytes to USB port: " + settings, ex);
                }
            }
        };

        InputStream inputStream = new MutableByteArrayInputStream() {
            @Override
            public synchronized int read() {
                int byteRead = -1;
                try {
                    List<UsbEndpoint> usbEndpoints = usbConnection.getUsbInterface().getUsbEndpoints();

                    for (UsbEndpoint usbEndpoint : usbEndpoints) {
                        if (usbEndpoint.getDirection() == UsbConst.ENDPOINT_DIRECTION_IN ) {
                            byte[] data = new byte[5];
                            UsbPipe pipe = usbEndpoint.getUsbPipe();
                            if (!pipe.isOpen()) {
                                pipe.open();
                                usbConnection.getInPipes().add(pipe);

                            }

                            UsbIrp packet = pipe.asyncSubmit(data);
//                            int bytesReceived = pipe.syncSubmit();

                            int bytesReceived = -1;

                            packet.waitUntilComplete(5000);

                            if (packet.isComplete()) {
                                if (packet.isUsbException()) {
                                    throw packet.getUsbException();
                                }

                                bytesReceived = packet.getActualLength();
                                data = packet.getData();
                                packet.complete();
                            }

                            if (usbEndpoint.getType() != UsbConst.ENDPOINT_TYPE_BULK) {
                                // for reasons not yet clear, we need to read from the "DATA" endpoint or
                                // face problems connecting to the Usb port next time.
                                // so for this case, we just throw away anything read.  It did not appear meaningful
                                // on NCR at least.
                                continue;
                            }

                            // it's also not clear why we need to read the last byte read, but it does have the data we need on NCR.
                            if (bytesReceived > 0) {
                                byteRead = (data[bytesReceived-1] & 0xFF);
                            }
                        }
                    }
                } catch (Exception ex) {
                    throw new PeripheralException("Failed to write bytes to USB port: " + settings, ex);
                }

                return byteRead;
            }
        };

        PeripheralConnection peripheralConnection = new PeripheralConnection();
        peripheralConnection.setRawConnection(usbConnection);
        peripheralConnection.setOut(outputStream);
        peripheralConnection.setIn(inputStream);
        return peripheralConnection;
    }

    @Override
    public void close(PeripheralConnection peripheralConnection) {
        if (peripheralConnection.getRawConnection() instanceof UsbConnection) {
            try {
                UsbConnection usbConnection = (UsbConnection) peripheralConnection.getRawConnection();
                usbConnection.close();
            } catch (Exception ex) {
                throw new PeripheralException("Failed to write bytes to USB port: " + settings, ex);
            }
        }
    }

    protected short getId(Map<String, Object> settings, String key) {
        Object value = settings.get(key);
        if (value instanceof String && (((String) value).equalsIgnoreCase("ANY") || ((String) value).equalsIgnoreCase("*"))
            || value == null) {
            return -1;
        } else if (value instanceof Integer) {
            return ((Integer)value).shortValue();
        } else {
            throw new PeripheralException("Don't know how to handle setting type for key: " + key +
                    ". Should be \"ANY\" or \"*\" or a hex id. Actual value was: '" + value + "'");
        }
    }

}
