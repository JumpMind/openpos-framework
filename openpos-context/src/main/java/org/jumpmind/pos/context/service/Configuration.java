package org.jumpmind.pos.context.service;

import org.jumpmind.pos.service.ServiceResult;

public class Configuration  extends ServiceResult {
    
    private String configName;
    private String configValue;
    private String configSource;
    
    public Configuration(String configName, String configValue) {
        this.configName = configName;
        this.configValue = configValue;
    }    
    
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
    public String getConfigSource() {
        return configSource;
    }
    public void setConfigSource(String configSource) {
        this.configSource = configSource;
    }

}
