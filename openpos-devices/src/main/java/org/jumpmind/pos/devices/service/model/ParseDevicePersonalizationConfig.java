package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ParseDevicePersonalizationConfig {
    private String deviceId;
    private String appId;
    private String personalizationParams;
    private String hostname;
}
