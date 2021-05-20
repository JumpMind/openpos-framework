package org.jumpmind.pos.util.event;

import lombok.ToString;

@ToString(callSuper = true)
public class DevicePairToggledEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    public DevicePairToggledEvent(String deviceId, String appId, String pairedDeviceId) {
        super(deviceId, appId, pairedDeviceId);
    }
}
