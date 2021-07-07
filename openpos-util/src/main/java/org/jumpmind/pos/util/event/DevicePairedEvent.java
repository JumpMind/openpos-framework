package org.jumpmind.pos.util.event;

import lombok.Getter;
import lombok.ToString;

@ToString(callSuper = true)
@Getter
public class DevicePairedEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    private String currentUsername;

    public DevicePairedEvent() {
        super();
    }

    public DevicePairedEvent(String deviceId, String appId, String pairedDeviceId, String currentUsername) {
        super(deviceId, appId, pairedDeviceId);
        this.currentUsername = currentUsername;
    }
}
