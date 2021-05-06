package org.jumpmind.pos.util.event;

import org.jumpmind.pos.util.event.AppEvent;

import lombok.ToString;

@ToString(callSuper = true)
public class DeviceConnectedEvent extends AppEvent {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private DeviceConnectedEvent() {}
    
    public DeviceConnectedEvent(String deviceId, String appId, String pairedDeviceId) {
        super(deviceId, appId, pairedDeviceId);
    }
}
