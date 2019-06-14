package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class Device implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private String deviceName;
    private boolean isOnline;
    
    public Device() {
        
    }
    
    public Device(String deviceName) {
        this.deviceName = deviceName;
    }
    
    public Device(String deviceName, boolean isOnline) {
        this(deviceName);
        this.isOnline = isOnline;
    }
    
    public String getDeviceName() {
        return deviceName;
    }
    
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }
    
    
    
}