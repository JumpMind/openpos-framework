package org.jumpmind.pos.util.event;

import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
@Data
public class ErrorEvent extends AppEvent {
    private Throwable throwable;

    public ErrorEvent(String deviceId, String appId, String pairedDeviceId, Throwable throwable) {
        super(deviceId, appId, pairedDeviceId);
        this.throwable = throwable;
    }
}
