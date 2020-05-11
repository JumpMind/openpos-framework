package org.jumpmind.pos.util.event;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class RejectionEvent<T> extends AppEvent {

    String targetDeviceId;
    String targetAppId;
    String request;

}
