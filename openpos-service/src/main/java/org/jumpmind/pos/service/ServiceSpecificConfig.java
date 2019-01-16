package org.jumpmind.pos.service;

import org.jumpmind.pos.service.strategy.InvocationiStrategy;

public class ServiceSpecificConfig extends ServiceCommonConfig implements Cloneable {

    protected String profile = ServiceConfig.LOCAL_PROFILE;
    protected InvocationiStrategy strategy = InvocationiStrategy.LOCAL_ONLY;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
    
    public boolean isLocal() {
        return ServiceConfig.LOCAL_PROFILE.equals(profile);
    }

    public InvocationiStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(InvocationiStrategy strategy) {
        this.strategy = strategy;
    }
    
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
