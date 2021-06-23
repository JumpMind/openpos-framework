package org.jumpmind.pos.util.event;

import lombok.Data;
import lombok.ToString;

@ToString(callSuper = true)
@Data
public class ErrorEvent extends AppEvent {
    private static final long serialVersionUID = 1L;

    private Throwable throwable;
    private String errorMessage;

    public ErrorEvent() {
        super();
    }

    public ErrorEvent(Throwable throwable) {
        super();
        this.throwable = throwable;
    }

    public ErrorEvent(String errorMessage) {
        super();
        this.errorMessage = errorMessage;
    }

    public ErrorEvent(String deviceId, String appId, String pairedDeviceId, String errorMessage) {
        super(deviceId, appId, pairedDeviceId);
        this.errorMessage = errorMessage;
    }

    public ErrorEvent() {
        super();
    }

    public ErrorEvent(String deviceId, String appId, String pairedDeviceId, Throwable throwable) {
        super(deviceId, appId, pairedDeviceId);
        this.throwable = throwable;
    }
}
