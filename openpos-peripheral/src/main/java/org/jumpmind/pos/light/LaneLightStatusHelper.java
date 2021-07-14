package org.jumpmind.pos.light;

import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LaneLightStatusHelper {
    
    private static final String CONFIG_PREFIX = "selfCheckout.laneLight.statuses.";
        
    @Autowired 
    ClientContext clientContext;
    
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
        String mappedStatus = clientContext.get(CONFIG_PREFIX + status.label); 
        if (mappedStatus != null) {
            return getStatus(mappedStatus);
        } else {
            return LaneLightStatus.OFF;
        }
    }

}
