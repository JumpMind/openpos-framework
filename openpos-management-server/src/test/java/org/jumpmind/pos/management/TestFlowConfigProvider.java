package org.jumpmind.pos.management;

import java.util.List;
import org.springframework.stereotype.Component;
import org.jumpmind.pos.core.flow.config.*;


@Component
public class TestFlowConfigProvider implements IFlowConfigProvider {

    public List<TransitionStepConfig> getTransitionStepConfig(String appId, String nodeId) {
        return null;
    }
    public FlowConfig getConfigByName(String appId, String nodeId, String name) {
        return null;
    }
    public FlowConfig getConfig(String appId, String nodeId) {
        return null;
    }
    public void load(String appId, String path, String startFlowName) {

    }

}
