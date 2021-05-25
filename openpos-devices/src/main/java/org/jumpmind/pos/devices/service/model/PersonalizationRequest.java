package org.jumpmind.pos.devices.service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PersonalizationRequest {
    private String deviceToken;
    private String deviceId;
    private String appId;
    private String pairedDeviceId;
    private Map<String, String> personalizationParameters;
}
