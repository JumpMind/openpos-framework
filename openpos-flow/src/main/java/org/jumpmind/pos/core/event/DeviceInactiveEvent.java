package org.jumpmind.pos.core.event;

import org.jumpmind.pos.util.event.AppEvent;

import lombok.ToString;

@ToString(callSuper = true)
public class DeviceInactiveEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    private DeviceInactiveEvent() {}
    
    public DeviceInactiveEvent(String deviceId, String appId) {
        super(deviceId, appId);
    }

}
