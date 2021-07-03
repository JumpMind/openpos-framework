package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.EndpointInvocationContext;
import org.jumpmind.pos.service.ServiceSpecificConfig;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface IInvocationStrategy {

    @Deprecated
    public default Object invoke(List<String> profileIds, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {
        EndpointInvocationContext endpointInvocationContext = EndpointInvocationContext.builder()
                .profileIds(profileIds)
                .strategy(this)
                .proxy(proxy)
                .method(method)
                .endpointsByPathMap(endpoints)
                .arguments(args).build();
        return invoke(endpointInvocationContext);
    }

    public Object invoke(EndpointInvocationContext endpointInvocationContext) throws Throwable;
    
    public String getStrategyName();
    
}
