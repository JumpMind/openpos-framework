package org.jumpmind.pos.core.flow;

import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.SubFlowConfig;
import org.jumpmind.pos.server.model.Action;

public class StateContext {

    private FlowConfig flowConfig;
    private Action action;
    private Object state;
    private Map<String, ScopeValue> flowScope = new HashMap<String, ScopeValue>(4);
    private SubFlowConfig subFlowConfig;
    
    public StateContext() {
        
    }
    
    public StateContext(FlowConfig flowConfig, Action action) {
        super();
        this.flowConfig = flowConfig;
        this.action = action;        
    }
    
    public StateContext(FlowConfig flowConfig, Action action, Object state) {
        super();
        this.flowConfig = flowConfig;
        this.action = action;
        this.state = state;
    }
    
    public boolean isSubstateReturnAction(String actionName) {
        if (subFlowConfig != null && subFlowConfig.getReturnActionNames() != null) {
            for (String returnActionName : subFlowConfig.getReturnActionNames()) {
                if (returnActionName.equals(actionName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getPrimarySubstateReturnAction() {
        if (subFlowConfig != null && subFlowConfig.getReturnActionNames() != null
                && subFlowConfig.getReturnActionNames().length == 1) {
            return subFlowConfig.getReturnActionNames()[0];
        } else {
            return null;
        }
    }
    
    public FlowConfig getFlowConfig() {
        return flowConfig;
    }

    public void setFlowConfig(FlowConfig flowConfig) {
        this.flowConfig = flowConfig;
    }
    
    public Action getAction() {
        return action;
    }
    public void setAction(Action action) {
        this.action = action;
    }
    public Object getState() {
        return state;
    }
    public void setState(Object state) {
        this.state = state;
    }
    
    public ScopeValue resolveScope(String name) {
        if (flowScope.containsKey(name)) {
            return flowScope.get(name);
        } else {
            return null;
        }
    }
    
    public Map<String, ScopeValue> getFlowScope() {
        return flowScope;
    }
    
    public void setFlowScope(String name, Object value) {
        flowScope.put(name, new ScopeValue(value));
    }
    
    public void removeFlowScope(String name) {
    	flowScope.remove(name);
    }

    public SubFlowConfig getSubFlowConfig() {
        return subFlowConfig;
    }

    public void setSubFlowConfig(SubFlowConfig subFlowConfig) {
        this.subFlowConfig = subFlowConfig;
    }

    @Override
    public String toString() {
        return state != null ? state.toString() : super.toString();
    }
    
    
}
