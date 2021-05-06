package org.jumpmind.pos.util.event;

import org.jumpmind.pos.util.event.AppEvent;

import lombok.ToString;

@ToString(callSuper = true)
public class DeviceDisconnectedEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private DeviceDisconnectedEvent() {}
    
    public DeviceDisconnectedEvent(String deviceId, String appId, String pairedDeviceId) {
        super(deviceId, appId, pairedDeviceId);
    }

}
