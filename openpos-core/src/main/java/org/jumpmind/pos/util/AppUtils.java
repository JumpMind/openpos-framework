package org.jumpmind.pos.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppUtils {
    
    static final Logger logger = LoggerFactory.getLogger(AppUtils.class);
       
    private AppUtils() {
    }
    
    public static boolean isDevMode() {
        String value = System.getProperty("profile");
        if (!StringUtils.isEmpty(value)) {
            return value.equalsIgnoreCase("dev");
        } else {
            return false;
        }
    }
    
}
