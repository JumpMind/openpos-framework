package org.jumpmind.pos.print;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.usb.UsbDevice;
import javax.usb.UsbEndpoint;
import javax.usb.UsbException;
import javax.usb.UsbInterface;
import javax.usb.UsbPipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsbConnection {

    static final Logger log = LoggerFactory.getLogger(UsbConnectionFactory.class);

    private UsbDevice usbDevice;
    private UsbEndpoint usbEndpoint;
    private UsbPipe usbPipe;
    private UsbInterface usbInterface;
    private List<UsbPipe> inPipes = new ArrayList<>();

    public UsbDevice getUsbDevice() {
        return usbDevice;
    }

    public void setUsbDevice(UsbDevice usbDevice) {
        this.usbDevice = usbDevice;
    }

    public UsbEndpoint getUsbEndpoint() {
        return usbEndpoint;
    }

    public void setUsbEndpoint(UsbEndpoint usbEndpoint) {
        this.usbEndpoint = usbEndpoint;
    }

    public UsbPipe getUsbPipe() {
        return usbPipe;
    }

    public void setUsbPipe(UsbPipe usbPipe) {
        this.usbPipe = usbPipe;
    }

    public UsbInterface getUsbInterface() {
        return usbInterface;
    }

    public void setUsbInterface(UsbInterface usbInterface) {
        this.usbInterface = usbInterface;
    }
    
    public List<UsbPipe> getInPipes() {
    	return inPipes;
    }

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
