package org.jumpmind.pos.util.event;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestEvent<T> extends AppEvent {

    String targetDeviceId;
    String targetAppId;
    String request;
    T payload;

    @Builder
    public RequestEvent(String deviceId, String appId, String pairedDeviceId, String targetDeviceId, String targetAppId, String request, T payload, boolean remote) {
        super(deviceId, appId, pairedDeviceId);
        this.targetDeviceId = targetDeviceId;
        this.targetAppId = targetAppId;
        this.request = request;
        this.payload = payload;
    }
}
