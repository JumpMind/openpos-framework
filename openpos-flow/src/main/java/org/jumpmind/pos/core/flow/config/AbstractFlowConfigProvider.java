package org.jumpmind.pos.core.flow.config;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractFlowConfigProvider implements IFlowConfigProvider {
    
    private Map<String, FlowConfig> flowConfigsByName = new HashMap<>();

    @Override
    public FlowConfig getConfigByName(String appId, String nodeId, String name) {
        buildFlowConfigsByName(appId, nodeId);
        return flowConfigsByName.get(name);
    }
    
    protected void buildFlowConfigsByName(String appId, String nodeId) {
        FlowConfig config = getConfig(appId, nodeId);
        buildFlowConfigsByName(appId, nodeId, config);
    }    
    
    protected void buildFlowConfigsByName(String appId, String nodeId, FlowConfig flowConfig) {
        flowConfigsByName.put(flowConfig.getName(), flowConfig);
        for (SubFlowConfig subFlowConfig : flowConfig.getActionToSubStateMapping().values()) {
            buildFlowConfigsByName(appId, nodeId, subFlowConfig.getSubFlowConfig()); // recurse.
        }
        
        for (StateConfig stateConfig : flowConfig.getStateConfigs().values()) {
            for (SubFlowConfig subFlowConfig : stateConfig.getActionToSubStateMapping().values()) {
                buildFlowConfigsByName(appId, nodeId, subFlowConfig.getSubFlowConfig()); // recurse.
            }
        }
    }

}
