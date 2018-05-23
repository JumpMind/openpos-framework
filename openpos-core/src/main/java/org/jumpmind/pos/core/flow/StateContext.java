package org.jumpmind.pos.core.flow;

import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.core.flow.config.FlowConfig;

public class StateContext {

    private FlowConfig flowConfig;
    private Action action;
    private IState state;
    private Map<String, ScopeValue> flowScope = new HashMap<String, ScopeValue>(4);
    
    public StateContext() {
        
    }
    
    public StateContext(FlowConfig flowConfig, Action action) {
        super();
        this.flowConfig = flowConfig;
        this.action = action;        
    }
    
    public StateContext(FlowConfig flowConfig, Action action, IState state) {
        super();
        this.flowConfig = flowConfig;
        this.action = action;
        this.state = state;
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
    public IState getState() {
        return state;
    }
    public void setState(IState state) {
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
    
    @Override
    public String toString() {
        return state != null ? state.toString() : super.toString();
    }
    
    
}
