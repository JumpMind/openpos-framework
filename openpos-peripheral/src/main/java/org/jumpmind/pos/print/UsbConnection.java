package org.jumpmind.pos.print;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.*;
import java.util.ArrayList;
import java.util.List;

@Data
public class UsbConnection {

    static final Logger log = LoggerFactory.getLogger(UsbConnectionFactory.class);

    private UsbDevice usbDevice;
    private UsbEndpoint usbEndpoint;
    private UsbPipe usbPipe;
    private UsbInterface usbInterface;
    private List<UsbPipe> inPipes = new ArrayList<>();

    public void close() {
        for (UsbPipe inPipe : inPipes) {
            try {
                inPipe.abortAllSubmissions();
                inPipe.close();
            } catch (UsbException ex) {
                log.warn("Failed to close USB in pipe.", ex);
            }
        }

        if (usbPipe != null) {
            try {
                usbPipe.abortAllSubmissions();
                usbPipe.close();
            } catch (Exception ex) {
                log.warn("Failed to close USB pipe.", ex);
            }
        }

        if (usbInterface != null) {
            try {
                usbInterface.release();
            } catch (Exception ex) {
                log.warn("Failed to release USB interface", ex);
            }
        }
    }
}
