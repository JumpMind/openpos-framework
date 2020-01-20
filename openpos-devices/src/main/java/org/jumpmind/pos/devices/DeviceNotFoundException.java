package org.jumpmind.pos.devices;

public class DeviceNotFoundException extends DevicesException {

    private static final long serialVersionUID = 1L;
    
    String deviceId;
    
    public DeviceNotFoundException(String deviceId) {
        super("A device with an id of " + deviceId + " cannot be found");
        this.deviceId = deviceId;
    }
    
    public String getDeviceId() {
        return deviceId;
    }
    
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

}
