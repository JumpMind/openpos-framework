package org.jumpmind.pos.service;

import org.jumpmind.pos.service.strategy.InvocationStrategy;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpecificConfig implements Cloneable {

    protected List<String> profileIds = null;
    protected InvocationStrategy strategy = InvocationStrategy.LOCAL_ONLY;
    protected List<EndpointSpecificConfig> endpoints = new ArrayList<>();

    public List<String> getProfileIds() {
        return profileIds;
    }

    public void setProfileIds(List<String> profileIds) {
        this.profileIds = profileIds;
    }

    public InvocationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(InvocationStrategy strategy) {
        this.strategy = strategy;
    }

    public List<EndpointSpecificConfig> getEndpoints() { return endpoints; }

    public void setEndpoints(List<EndpointSpecificConfig> endpoints) { this.endpoints = endpoints; }

    public ServiceSpecificConfig copy() {
        ServiceSpecificConfig copy;
        try {
            copy = (ServiceSpecificConfig)this.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
