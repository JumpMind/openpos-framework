package org.jumpmind.pos.core.event;

import org.jumpmind.pos.util.event.AppEvent;

public class DeviceHeartbeatEvent extends AppEvent {

    private static final long serialVersionUID = 1L;

    
    @SuppressWarnings("unused")
    private DeviceHeartbeatEvent() {
    }
    
    public DeviceHeartbeatEvent(String deviceId) {
        super(deviceId, null);
    }
    
}
