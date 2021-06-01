package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.util.model.Message;

public interface IMessageInterceptor<T extends Message> {

    void intercept(String nodeId, T message);
}
