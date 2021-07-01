package org.jumpmind.pos.service;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.service.strategy.IInvocationStrategy;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class EndpointInvocationContext {

    private IInvocationStrategy strategy;
    List<String> profileIds;
    ServiceSpecificConfig config;
    Object proxy;
    Method method;
    String endpointImplementation;
    Map<String, Object> endpointsByPathMap;
    private String clientVersion;
    private String endpointPath;
    private Object endpoint;
    private Object[] arguments;
    private Object result;

}
