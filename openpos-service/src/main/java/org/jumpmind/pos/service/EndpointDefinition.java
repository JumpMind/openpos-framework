package org.jumpmind.pos.service;

import java.lang.reflect.Method;

public class EndpointDefinition {

    private String requestPath;
    private Object endpointInstance;
    private Method endpointMethod;
    private boolean isOverride;
    
    public String getRequestPath() {
        return requestPath;
    }
    public void setRequestPath(String requestPath) {
        this.requestPath = requestPath;
    }
    public Object getEndpointInstance() {
        return endpointInstance;
    }
    public void setEndpointInstance(Object endpointInstance) {
        this.endpointInstance = endpointInstance;
    }
    public boolean isOverride() {
        return isOverride;
    }
    public void setOverride(boolean isOverride) {
        this.isOverride = isOverride;
    }
    public Method getEndpointMethod() {
        return endpointMethod;
    }
    public void setEndpointMethod(Method endpointMethod) {
        this.endpointMethod = endpointMethod;
    }
    
}
