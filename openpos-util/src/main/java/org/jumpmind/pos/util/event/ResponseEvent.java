package org.jumpmind.pos.util.event;

import lombok.Builder;
import lombok.Data;

@Data
public class ResponseEvent<T> extends AppEvent {

    String targetDeviceId;
    String targetAppId;
    String request;
    T payload;

    @Builder
    public ResponseEvent(String deviceId, String appId, boolean remote, String targetDeviceId, String targetAppId, String request, T payload) {
        super(deviceId, appId, remote);
        this.targetDeviceId = targetDeviceId;
        this.targetAppId = targetAppId;
        this.request = request;
        this.payload = payload;
    }
}
