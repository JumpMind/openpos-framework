package org.jumpmind.pos.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;

@Component
public class EndpointRegistry {

    @Autowired
    private ConfigurableApplicationContext applicationContext;
        
    private Map<String, EndpointDefinition> baseEndpoints = new HashMap<>();
    private Map<String, EndpointDefinition> overrideEndpoints = new HashMap<>();
    
    @EventListener(ContextRefreshedEvent.class)
    public void initEndpoints() {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            if (bean != null) {
                checkAndRegisterEndpoint(bean);
            }
        }
    }
    
    protected void checkAndRegisterEndpoint(Object bean) {
        Class<?> beanClass = bean.getClass();
        
        for (Method method : beanClass.getDeclaredMethods()) {
            Endpoint baseEndpoint = AnnotationUtils.findAnnotation(method, Endpoint.class);
            EndpointOverride overrideEndpoint = AnnotationUtils.findAnnotation(method, EndpointOverride.class);
            
            if (baseEndpoint != null) {
                baseEndpoints.put(baseEndpoint.value(), buildEndpointDefition(baseEndpoint.value(), method, bean));
            } else if (overrideEndpoint != null) {
                overrideEndpoints.put(overrideEndpoint.value(), buildEndpointDefition(overrideEndpoint.value(), method, bean));
            }
        }
    }

    public EndpointDefinition findOverrideEndpointDefinition(String requestPath) {
        return overrideEndpoints.get(requestPath);
    }

    public EndpointDefinition findBaseEndpointDefinition(String requestPath) {
        return baseEndpoints.get(requestPath);
    }
    
    protected EndpointDefinition buildEndpointDefition(String requestPath, Method method, Object bean) {
        EndpointDefinition endpointDefinition = new EndpointDefinition();
        endpointDefinition.setEndpointInstance(bean);
        endpointDefinition.setEndpointMethod(method);
        endpointDefinition.setRequestPath(requestPath);
        endpointDefinition.setOverride(method.getAnnotation(EndpointOverride.class) != null);
        return endpointDefinition;
    }    
}
