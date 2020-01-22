package org.jumpmind.pos.devices.service.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class GetDeviceRequest {
    private String deviceId;
    private String appId;
}
