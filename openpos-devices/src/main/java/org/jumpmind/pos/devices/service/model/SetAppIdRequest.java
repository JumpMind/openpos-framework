package org.jumpmind.pos.devices.service.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetAppIdRequest {
    private String deviceId;
    private String newAppId;
}
