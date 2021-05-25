package org.jumpmind.pos.util.event;

import lombok.ToString;

@ToString(callSuper = true)
public class DevicePairedEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    public DevicePairedEvent(String deviceId, String appId, String pairedDeviceId) {
        super(deviceId, appId, pairedDeviceId);
    }
}
