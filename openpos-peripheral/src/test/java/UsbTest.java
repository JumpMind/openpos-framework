import org.usb4java.*;

import javax.usb.*;
import java.nio.ByteBuffer;
import java.util.List;

public class UsbTest  {

    public static void main(String[] args) throws Exception {


        int result = LibUsb.init(null);
        if (result != LibUsb.SUCCESS){
            throw new LibUsbException("Unable to initialze libusb",result);
        }

        UsbDevice device = findDevice(UsbHostManager.getUsbServices().getRootUsbHub(), (short)0x0404, (short)0x0311);
        System.out.println("Device: " + device);



//        UsbConfiguration configuration = (UsbConfiguration)device.getUsbConfigurations().get(0);
        UsbConfiguration configuration = (UsbConfiguration)device.getActiveUsbConfiguration();

        UsbInterface iface = configuration.getUsbInterface((byte) 0);
        iface.claim();
        try
        {
            UsbEndpoint endpoint = iface.getUsbEndpoint((byte)0x14210000);
            UsbPipe pipe = endpoint.getUsbPipe();
            pipe.open();
            try
            {
                int sent = pipe.syncSubmit("Hello".getBytes());
                System.out.println(sent + " bytes sent");
            }
            finally
            {
                pipe.close();
            }
        }
        finally
        {
            iface.release();
        }


//        Device device = findDevice((short)0x0404, (short)0x0311);
//        System.out.println("Device: " + device);
//
//        DeviceHandle handle = new DeviceHandle();
//        result = LibUsb.open(device, handle);
//        if (result != LibUsb.SUCCESS) {
//            throw new LibUsbException("Unable to open USB device", result);
//        }
//        try
//        {
//            ByteBuffer buffer = ByteBuffer.allocateDirect(8);
//            buffer.put( "Hello\n".getBytes());
//            int transfered = LibUsb.controlTransfer(handle,
//                    (byte) (LibUsb.ENDPOINT_OUT | LibUsb.RECIPIENT_INTERFACE),
//                    (byte) 0x09, (short) 2, (short) 1, buffer, 60000);
//            if (transfered < 0) {
//                throw new LibUsbException("Control transfer failed", transfered);
//            }
//            System.out.println(transfered + " bytes sent");
//        }
//        finally
//        {
//            LibUsb.close(handle);
//        }

//        UsbConfiguration configuration = device.getActiveUsbConfiguration();
//        UsbInterface iface = configuration.getUsbInterface((byte) 1);
//        iface.claim();
//        try
//        {
//    ... Communicate with the interface or endpoints ...
//        }
//        finally
//        {
//            iface.release();
//        }
//
//
//        UsbEndpoint endpoint = iface.getUsbEndpoint(0x03);
//        UsbPipe pipe = endpoint.getUsbPipe();
//        pipe.open();
//        try
//        {
//            int sent = pipe.syncSubmit("Hello world".getBytes());
//            System.out.println(sent + " bytes sent");
//        }
//        finally
//        {
//            pipe.close();
//        }
    }

    public static UsbDevice findDevice(UsbHub hub, short vendorId, short productId)
    {
        for (UsbDevice device : (List<UsbDevice>) hub.getAttachedUsbDevices())
        {
            UsbDeviceDescriptor desc = device.getUsbDeviceDescriptor();
            if (desc.idVendor() == vendorId && desc.idProduct() == productId) return device;
            if (device.isUsbHub())
            {
                device = findDevice((UsbHub) device, vendorId, productId);
                if (device != null) return device;
            }
        }
        return null;
    }

    public static Device findDevice(short vendorId, short productId)
    {
        // Read the USB device list
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(null, list);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);

        try
        {
            // Iterate over all devices and scan for the right one
            for (Device device: list)
            {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) {
                    throw new LibUsbException("Unable to read device descriptor", result);
                }
                System.out.println(descriptor);
                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId) {
                    return device;
                }
            }
        }
        finally
        {
            // Ensure the allocated device list is freed
            LibUsb.freeDeviceList(list, true);
        }

        // Device not found
        return null;
    }
}
