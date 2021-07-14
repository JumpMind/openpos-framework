package org.jumpmind.pos.light;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@ConfigurationProperties(prefix = "self-checkout.lane-light")
public class LaneLightStatusHelper {
            
    private final Map<String, String> statuses = new HashMap<>();

    public Map<String, String> getStatuses() {
        return statuses;
    }
    
    public LaneLightStatus getStatus(String status) {
        try {
            LaneLightStatus val = LaneLightStatus.valueOf(status);
            return val;
        } catch (IllegalArgumentException e) {
            log.warn("Could not find a lane light status mapping for \'" + status + "\'", e);
            return LaneLightStatus.OFF;
        }
    }
    
    public LaneLightStatus getStatusFromSelfCheckoutStatus(SelfCheckoutStatus status) {
        String mappedStatus = statuses.get(status.label); 
        if (mappedStatus != null) {
            return getStatus(mappedStatus);
        } else {
            return LaneLightStatus.OFF;
        }
    }

}
