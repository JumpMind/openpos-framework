package org.jumpmind.pos.util.event;

public class AppEvent extends Event {

    String deviceId;
    String appId;

    public AppEvent(String deviceId, String appId) {
        super(createSourceString(deviceId, appId));
        this.deviceId = deviceId;
        this.appId = appId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public static String createSourceString(String appId, String deviceId) {
        return appId + "/" + deviceId;
    }
}
