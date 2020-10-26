package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.util.model.Message;

public interface IMessageInterceptor<T extends Message> {
    public static final int DEFAULT_ORDER = 0;

    default int order() {return DEFAULT_ORDER;}
    void intercept(String appId, String nodeId, T message);
}
