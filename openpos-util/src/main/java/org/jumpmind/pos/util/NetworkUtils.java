package org.jumpmind.pos.util;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NetworkUtils {
    static final Logger logger = LoggerFactory.getLogger(NetworkUtils.class);

    static AtomicReference<String> FULLY_QUALIFIED_HOSTNAME = new AtomicReference<String>(null);

    /**
     * Reports whether or not the given TCP port is already in use.
     * @param port The port to check
     * @return {@code true} if the port is available, {@code false} if it is not.
     */
    public static boolean isTcpPortAvailable(int port) {
        try (ServerSocket serverSocket = new ServerSocket()) {
            serverSocket.setReuseAddress(false);
            serverSocket.bind(new InetSocketAddress(InetAddress.getByName("localhost"), port), 1);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * Attempts to return the fully qualified domain name of localhost.
     * @return
     */
    public static String getNetworkHostname() {
        if (FULLY_QUALIFIED_HOSTNAME.get() == null) {
            try {
                String hostName = InetAddress.getLocalHost().getCanonicalHostName();
                
                if (StringUtils.isBlank(hostName)) {
                    hostName = InetAddress.getByName(
                            InetAddress.getLocalHost().getHostAddress()).getHostName();
                }
                
                if (StringUtils.isNotBlank(hostName)) {
                    hostName = hostName.trim();
                }
                FULLY_QUALIFIED_HOSTNAME.compareAndSet(null, hostName);
            } catch (Exception ex) {
                logger.info("Unable to lookup hostname: " + ex);
            }
        }
        return FULLY_QUALIFIED_HOSTNAME.get();
    }
}
