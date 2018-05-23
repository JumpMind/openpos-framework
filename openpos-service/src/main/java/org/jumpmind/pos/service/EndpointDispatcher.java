package org.jumpmind.pos.service;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndpointDispatcher {

    @Autowired
    private EndpointRegistry endpointRegistry;

    @SuppressWarnings("unchecked")
    public <T> T dispatch(String path, Object... args) {
        EndpointDefinition baseEndpointDefinition = endpointRegistry.findBaseEndpointDefinition(path);
        EndpointDefinition overrideEndpointDefinition = endpointRegistry.findOverrideEndpointDefinition(path);
        
        Object result = null;
        
        if (overrideEndpointDefinition != null) {
            result = invokeEndpoint(overrideEndpointDefinition, baseEndpointDefinition, args);
        } else if (baseEndpointDefinition != null) {
            result = invokeEndpoint(baseEndpointDefinition, null, args);
        } else {
            throw noEndpointFound(path);
        }
        
        return (T)result;
    }
    
    protected Object invokeEndpoint(EndpointDefinition invokeEndpointDefinition,
            EndpointDefinition baseEndpointDefinition,
            Object...args) {
        Method method = invokeEndpointDefinition.getEndpointMethod();
        
        List<Object> parameters = new ArrayList<>();
        
        int index = 0;
        for (Class<?> expectedType : method.getParameterTypes()) {
            if (baseEndpointDefinition != null 
                    && expectedType.isAssignableFrom(baseEndpointDefinition.getEndpointInstance().getClass())) {
                parameters.add(baseEndpointDefinition.getEndpointInstance());
                index--;
            } else if (index < args.length) {
                parameters.add(args[index]);
            }
            
            index++;
        }
        
        Object result;
        try {
            result = method.invoke(invokeEndpointDefinition.getEndpointInstance(), parameters.toArray());
        } catch (Exception ex) {
            throw failedToInvokeEndpoint(invokeEndpointDefinition, parameters, ex);
        }
        
        return result;
    }

    protected RuntimeException failedToInvokeEndpoint(EndpointDefinition baseEndpointDefinition, List<Object> parameters, Exception cause) {
        Method method = baseEndpointDefinition.getEndpointMethod();
        String msg = String.format("Failed to invoke endpoint method \"%s\".\nExected argument types %s\nActual parameters %s",
                method, Arrays.asList(method.getParameterTypes()), parameters);
        throw new PosServerException(msg, cause);
    }

    protected RuntimeException noEndpointFound(String path) {
        throw new PosServerException(String.format("No endpoint found for path '%s' Please define a Spring-discoverable @Componant class, "
                + "with a method annotated like  @Endpoint(\"%s\")", path, path));
    }
    
}
