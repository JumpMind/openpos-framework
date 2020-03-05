package org.jumpmind.pos.core.event;

import org.jumpmind.pos.util.event.AppEvent;

public class DeviceConnectedEvent extends AppEvent {

    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private DeviceConnectedEvent() {}
    
    public DeviceConnectedEvent(String deviceId, String appId) {
        super(deviceId, appId);
    }
}
