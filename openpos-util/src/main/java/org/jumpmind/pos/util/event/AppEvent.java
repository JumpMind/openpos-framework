package org.jumpmind.pos.util.event;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class AppEvent extends Event implements Serializable {
    private static final long serialVersionUID = 1L;

    String deviceId;
    String appId;
    String pairedDeviceId;
    boolean remote;

    public AppEvent() {
      super();
    }

    public AppEvent(String deviceId, String appId) {
        this(deviceId, appId, false);
    }

    public AppEvent(String deviceId, String appId, String pairedDeviceId) { this(deviceId, appId, pairedDeviceId, false); }

    public AppEvent(String deviceId, String appId, boolean remote) {
        super(createSourceString(appId, deviceId));
        this.deviceId = deviceId;
        this.appId = appId;
    }

    public AppEvent(String deviceId, String appId, String pairedDeviceId, boolean remote) {
        super(createSourceString(appId, deviceId, pairedDeviceId));
        this.deviceId = deviceId;
        this.appId = appId;
        this.pairedDeviceId = pairedDeviceId;
    }

    public static String createSourceString(String appId, String deviceId) {
        return appId + "/" + deviceId;
    }

    public static String createSourceString(String appId, String deviceId, String pairedDeviceId) {
        return appId + "/" + deviceId + "/" + pairedDeviceId;
    }
}
