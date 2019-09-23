package org.jumpmind.pos.core.flow;

import org.springframework.stereotype.Component;

@Component
public class FlowEventBus {

    public void publish(FlowEvent event) {

    }

    public void register(String deviceId, IFlowEventListener listener) {

    }

    public void unregister(String deviceId, IFlowEventListener listener) {

    }
}
