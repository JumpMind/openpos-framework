package org.jumpmind.pos.core.flow;

import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.SubTransition;
import org.jumpmind.pos.server.model.Action;

public class StateContext {

    private FlowConfig flowConfig;
    private Action action;
    private Object state;
    private Map<String, ScopeValue> flowScope = new HashMap<String, ScopeValue>(4);
    private SubTransition subTransition;
    
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
        if (subTransition != null && subTransition.getReturnActionNames() != null) {
            for (String returnActionName : subTransition.getReturnActionNames()) {
                if (returnActionName.equals(actionName)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getPrimarySubstateReturnAction() {
        if (subTransition != null && subTransition.getReturnActionNames() != null
                && subTransition.getReturnActionNames().length == 1) {
            return subTransition.getReturnActionNames()[0];
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

    public SubTransition getSubTransition() {
        return subTransition;
    }

    public void setSubTransition(SubTransition subTransition) {
        this.subTransition = subTransition;
    }

    @Override
    public String toString() {
        return state != null ? state.toString() : super.toString();
    }
    
    
}
