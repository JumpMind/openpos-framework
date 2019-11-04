package org.jumpmind.pos.util;

import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class AppUtils {
    
    static final Logger logger = LoggerFactory.getLogger(AppUtils.class);
    
    static AtomicReference<String> HOST_NAME = new AtomicReference<String>(null);
        
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
    
    public static String getHostName() {
        if (HOST_NAME.get() == null) {
            try {
                String hostName = System.getenv("HOSTNAME");
                
                if (StringUtils.isBlank(hostName)) {
                    hostName = System.getenv("COMPUTERNAME");
                }

                if (StringUtils.isBlank(hostName)) {
                    try {
                        hostName = IOUtils.toString(Runtime.getRuntime().exec("hostname").getInputStream(), Charset.defaultCharset());
                    } catch (Exception ex) {}
                }
                
                if (StringUtils.isBlank(hostName)) {
                    hostName = InetAddress.getByName(
                            InetAddress.getLocalHost().getHostAddress()).getHostName();
                }
                
                if (StringUtils.isNotBlank(hostName)) {
                    hostName = hostName.trim();
                }
                HOST_NAME.compareAndSet(null, hostName);
            } catch (Exception ex) {
                logger.info("Unable to lookup hostname: " + ex);
            }
        }
        return HOST_NAME.get();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            logger.debug("Thread sleep interrupted.", ex);
        }
    }
    
    
}
