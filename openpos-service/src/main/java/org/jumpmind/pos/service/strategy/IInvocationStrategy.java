package org.jumpmind.pos.service.strategy;

import java.lang.reflect.Method;

import org.jumpmind.pos.service.ServiceSpecificConfig;

public interface IInvocationStrategy {

    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Object[] args) throws Throwable;
    
}
