package org.jumpmind.pos.util;

import java.io.File;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;
import java.net.URL;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.slf4j.MDC;
import static org.apache.commons.lang3.StringUtils.*;

@Slf4j
public final class AppUtils {

    static AtomicReference<String> HOST_NAME = new AtomicReference<String>(null);

    private static FastDateFormat timezoneFormatter = FastDateFormat.getInstance("Z");
        
    private AppUtils() {
    }

    public static void setupLogging(String deviceId) {
        if (isNotBlank(deviceId)) {
            MDC.put("stateManager", String.format("%s", deviceId));
            MDC.put("deviceId", deviceId);
        }
    }
    
    public static boolean isDevMode() {
        String value = System.getProperty("profile");
        if (!StringUtils.isEmpty(value)) {
            return value.equalsIgnoreCase("dev");
        } else {
            return false;
        }
    }

    public static String getTimezoneOffset() {
        String tz = timezoneFormatter.format(new Date());
        if (tz != null && tz.length() == 5) {
            return tz.substring(0, 3) + ":" + tz.substring(3, 5);
        }
        return null;
    }

    public static int getAvailableProcessors() {
        int numberOfCores = -1;
        try {
            numberOfCores = Runtime.getRuntime().availableProcessors();
            if (numberOfCores > 0) {
                return numberOfCores;
            } else {
                log.warn("Number of cores reported: " + numberOfCores + " - defaulting to 1");
                return 1;
            }
        } catch (Exception ex) {
            log.warn("Failed to determine number of cores on this system.", ex);
            return 1;
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
                log.info("Unable to lookup hostname: " + ex);
            }
        }
        return HOST_NAME.get();
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ex) {
            log.debug("Thread sleep interrupted.", ex);
        }
    }
}
