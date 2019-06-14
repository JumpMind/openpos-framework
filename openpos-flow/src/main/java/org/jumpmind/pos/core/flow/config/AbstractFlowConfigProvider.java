package org.jumpmind.pos.core.flow.config;

import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.IFlowConfigProvider;
import org.jumpmind.pos.core.flow.config.StateConfig;
import org.jumpmind.pos.core.flow.config.SubTransition;

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
        for (SubTransition subTransition : flowConfig.getActionToSubStateMapping().values()) {
            buildFlowConfigsByName(appId, nodeId, subTransition.getSubFlowConfig()); // recurse.
        }
        
        for (StateConfig stateConfig : flowConfig.getStateConfigs().values()) {
            for (SubTransition subTransition : stateConfig.getActionToSubStateMapping().values()) {
                buildFlowConfigsByName(appId, nodeId, subTransition.getSubFlowConfig()); // recurse.    
            }
        }
    }

}
