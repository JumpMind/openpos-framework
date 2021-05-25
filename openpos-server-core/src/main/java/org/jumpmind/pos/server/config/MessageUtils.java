package org.jumpmind.pos.server.config;

import java.util.List;
import java.util.Map;

import org.springframework.messaging.Message;

public interface MessageUtils {
    public final static String COMPATIBILITY_VERSION_HEADER = "compatibilityVersion";
    public final static String QUERY_PARAMS_HEADER = "queryParams";
    public final static String APPID_HEADER = "appId";
    
    /**
     * Get message header value with value in name from the given message
     */
    default public String getHeader(Message<?> message, String name) {
        @SuppressWarnings("unchecked")
        Map<String, List<String>> nativeHeaders = (Map<String, List<String>>) message.getHeaders().get("nativeHeaders");
        List<String> values = nativeHeaders.get(name);
        return values != null && values.size() > 0 ? values.get(0) : null;
    }

}
