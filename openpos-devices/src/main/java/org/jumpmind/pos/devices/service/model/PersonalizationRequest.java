package org.jumpmind.pos.devices.service.model;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class PersonalizationRequest {
    private String personalizationToken;
    private String deviceId;
    private String appId;
    private Map<String, String> personalizationParameters;
}
