package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnpairDeviceRequest {
    private String deviceId;
    private String appId;
    private String pairedDeviceId;
    private String pairedAppId;
}
