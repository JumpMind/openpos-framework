package org.jumpmind.pos.service;

import org.jumpmind.pos.service.strategy.InvocationStrategy;

public class EndpointSpecificConfig implements Cloneable {

    protected String profile;
    protected InvocationStrategy strategy;
    protected String path;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public InvocationStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(InvocationStrategy strategy) {
        this.strategy = strategy;
    }

    public EndpointSpecificConfig copy() {
        EndpointSpecificConfig copy;
        try {
            copy = (EndpointSpecificConfig)this.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }


}
