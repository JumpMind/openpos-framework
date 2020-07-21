package org.jumpmind.pos.core.error;

public class IncidentContext {

    private String deviceId;

    public IncidentContext(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
