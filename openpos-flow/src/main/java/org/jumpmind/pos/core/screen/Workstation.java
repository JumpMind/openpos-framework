package org.jumpmind.pos.core.screen;

import java.io.Serializable;

public class Workstation implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    String storeId;
    
    String workstationId;
    
    int tillThresholdStatus;
    
    public Workstation() {
    }

    public Workstation(String storeId, String workstationId) {
        this.storeId = storeId;
        this.workstationId = workstationId;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getWorkstationId() {
        return workstationId;
    }

    public void setWorkstationId(String workstationId) {
        this.workstationId = workstationId;
    }
    
    public int getTillThresholdStatus() {
        return tillThresholdStatus;
    }
    
    public void setTillThresholdStatus(int tillThresholdStatus) {
        this.tillThresholdStatus = tillThresholdStatus;
    }
    
    
}
