package org.jumpmind.pos.util.event;

import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
@Data
public class DevicePairedEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    private String currentUsername;

    public DevicePairedEvent(String deviceId, String appId, String pairedDeviceId) {
        super(deviceId, appId, pairedDeviceId);
    }

    public DevicePairedEvent(String deviceId, String appId, String pairedDeviceId, String currentUsername) {
        super(deviceId, appId, pairedDeviceId);
        this.currentUsername = currentUsername;
    }
}
