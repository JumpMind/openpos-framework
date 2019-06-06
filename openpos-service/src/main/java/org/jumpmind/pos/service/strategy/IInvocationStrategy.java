package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.ServiceSpecificConfig;

import java.lang.reflect.Method;
import java.util.Map;

public interface IInvocationStrategy {

    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable;
    
    public String getStrategyName();
    
}
