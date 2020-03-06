package org.jumpmind.pos.core.event;

import org.jumpmind.pos.util.event.AppEvent;

import lombok.ToString;

@ToString
public class DeviceDisconnectedEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private DeviceDisconnectedEvent() {}
    
    public DeviceDisconnectedEvent(String deviceId, String appId) {
        super(deviceId, appId);
    }

}
