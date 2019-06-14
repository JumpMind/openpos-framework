package org.jumpmind.pos.core.service.spring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jumpmind.pos.core.flow.FlowException;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.core.flow.StateManagerContainer;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.stereotype.Component;

@Component
public class DeviceScope implements Scope {
    
    private ConfigurableListableBeanFactory factory;
    
    private Map<String, Runnable> destructionCallbacks
      = Collections.synchronizedMap(new HashMap<String, Runnable>());
    
    private static final Set<String> deviceScopedBeanNames = ConcurrentHashMap.newKeySet();
    
    public DeviceScope(ConfigurableListableBeanFactory factory) {
        this.factory = factory;
    }
    
    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        
        IStateManager stateManager = getStateManager();
        
        if (!stateManager.getApplicationState().getScope().getDeviceScope().containsKey(name)) {
            deviceScopedBeanNames.add(name);
            Object bean = objectFactory.getObject();
            stateManager.getApplicationState().getScope().setScopeValue(ScopeType.Device, name, bean);
        }
        
        return stateManager.getApplicationState().getScopeValue(ScopeType.Device, name);
    }
    
    @Override
    public Object remove(String name) {
        IStateManager stateManager = getStateManager();
        destructionCallbacks.remove(name);
        Object bean = stateManager.getApplicationState().getScopeValue(ScopeType.Device, name);
        stateManager.getApplicationState().getScope().removeDeviceScope(name);
        return bean;
    }
    
    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        destructionCallbacks.put(name, callback);
    }
    
    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }
    
    @Override
    public String getConversationId() {
        return getStateManager().getDeviceId();
    }
    
    public static boolean isDeviceScope(String beanName) {
        return deviceScopedBeanNames.contains(beanName);
    }
    
    protected IStateManager getStateManager() {
        StateManagerContainer stateManagerContainer = factory.getBean(StateManagerContainer.class);
        
        IStateManager stateManager = stateManagerContainer.getCurrentStateManager();
        if (stateManager == null) {
            throw new FlowException("Illegal use of custom spring \"device\" scope. There is no current statemanager. "
                    + "This scope can only function when stateManagerContainer.getCurrentStateManager() returns non-null.");
        }
        
        return stateManager;
    }
    
}