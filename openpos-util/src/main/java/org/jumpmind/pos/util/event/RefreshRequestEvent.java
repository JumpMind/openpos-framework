package org.jumpmind.pos.util.event;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshRequestEvent extends AppEvent {

    @Builder
    public RefreshRequestEvent(String deviceId, String appId, String pairedDeviceId, boolean remote) {
        super(deviceId, appId, pairedDeviceId, remote);
    }
}
