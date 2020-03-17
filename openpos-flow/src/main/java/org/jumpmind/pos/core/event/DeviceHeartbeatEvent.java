package org.jumpmind.pos.core.event;

import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.ITransientEvent;

/**
 * DevicHeartbeatEvents are published periodically to indicate that a device is 
 * alive. They are not intended to be distributed beyond the local Java Virtual
 * Machine from which they originate and are used for notifying code implemented
 * in other OpenPOS modules that a device is alive.
 */
public class DeviceHeartbeatEvent extends AppEvent implements ITransientEvent {

    private static final long serialVersionUID = 1L;

    
    @SuppressWarnings("unused")
    private DeviceHeartbeatEvent() {
    }
    
    public DeviceHeartbeatEvent(String deviceId, String appId) {
        super(deviceId, appId);
    }
    
}
