package org.jumpmind.pos.context.service;

import org.jumpmind.pos.context.model.ConfigModel;
import org.jumpmind.pos.service.ServiceResult;

public class ConfigResult extends ServiceResult {

    private String configName;
    private String configValue;
    private ConfigModel config;
    
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
    public ConfigModel getConfig() {
        return config;
    }
    public void setConfig(ConfigModel config) {
        this.config = config;
    }
    
}
