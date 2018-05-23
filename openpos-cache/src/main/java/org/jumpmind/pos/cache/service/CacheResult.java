package org.jumpmind.pos.cache.service;

import org.jumpmind.pos.service.ServiceResult;

public class CacheResult extends ServiceResult {

    private String configName;
    private String configValue;
    private CacheResult config;
    
    public String getConfigName() {
        return configName;
    }
    public void setConfigName(String configName) {
        this.configName = configName;
    }
    public String getConfigValue() {
        return configValue;
    }
    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }
    public CacheResult getConfig() {
        return config;
    }
    public void setConfig(CacheResult config) {
        this.config = config;
    }
    
}
