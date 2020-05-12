package org.jumpmind.pos.util.event;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RefreshRequestEvent extends AppEvent {

    @Builder
    public RefreshRequestEvent(String deviceId, String appId, boolean remote) {
        super(deviceId, appId, remote);
    }
}
