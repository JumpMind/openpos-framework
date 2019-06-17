package org.jumpmind.pos.core.model;

import java.io.Serializable;
import java.util.List;

public class SystemStatus implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private SystemStatusType overallSystemStatus;
    private List<Device> devices;
    
    public SystemStatusType getOverallSystemStatus() {
        return overallSystemStatus;
    }
    
    public void setOverallSystemStatus(SystemStatusType overallSystemStatus) {
        this.overallSystemStatus = overallSystemStatus;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }
       
}