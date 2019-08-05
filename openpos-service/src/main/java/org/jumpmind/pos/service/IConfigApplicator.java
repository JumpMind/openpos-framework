package org.jumpmind.pos.service;

import java.util.Map;

public interface IConfigApplicator {

    public void applyAdditionalConfiguration(String deviceId, String startsWith, Object applyTo);
    
    public <T> Map<String, T> getAdditionalConfigurationMap(String deviceId, String startsWith, T configType);
        
}
