package org.jumpmind.pos.light;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaneLightStatusHelper {
    
    protected static final Logger log = LoggerFactory.getLogger(LaneLightStatusHelper.class);
    
    public static LaneLightStatus getStatus(String status) {
        try {
            LaneLightStatus val = LaneLightStatus.valueOf(status);
            return val;
        } catch (IllegalArgumentException e) {
            log.warn("Could not find a lane light status mapping for \'" + status + "\'", e);
            return LaneLightStatus.OFF;
        }
    }

}
