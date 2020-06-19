package org.jumpmind.pos.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openpos.services")
@Data
public class ServiceConfig {

    public static String LOCAL_PROFILE = "local";

    protected Map<String, ProfileConfig> profiles;
    protected Map<String, ServiceSpecificConfig> specificConfig;

    @Autowired(required = false)
    IConfigApplicator additionalConfigSource;

    public Map<String, ProfileConfig> getProfiles() {
        if (profiles == null) {
            profiles = new HashMap<>();
        }
        return profiles;
    }

    public void setProfiles(Map<String, ProfileConfig> profiles) {
        this.profiles = profiles;
    }

    public void setSpecificConfig(Map<String, ServiceSpecificConfig> specificConfig) {
        this.specificConfig = specificConfig;
    }

    public Map<String, ServiceSpecificConfig> getSpecificConfig() {
        if (specificConfig == null) {
            specificConfig = new HashMap<>();
        }
        return specificConfig;
    }

    //TODO: add this for profile
    public ServiceSpecificConfig getServiceConfig(String serviceId) {
        ServiceSpecificConfig config = getSpecificConfig().get(serviceId);
        if (config == null) {
            config = new ServiceSpecificConfig();
        } else {
            config = config.copy();
        }

        if (additionalConfigSource != null) {
            additionalConfigSource.applyAdditionalConfiguration(String.format("openpos.services.specificConfig.%s", serviceId), config);
        }

        return config;
    }

}
