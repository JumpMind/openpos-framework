package org.jumpmind.pos.service;


import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "/serviceConfig", key = "#deviceId.concat('-').concat(#serviceId)")
    public ServiceSpecificConfig getServiceConfig(String deviceId, String serviceId) {
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

    public ProfileConfig getProfileConfig(String profileId) {
        ProfileConfig config = getProfiles().get(profileId);
        if (config == null) {
            config = new ProfileConfig();
        } else {
            config = config.copy();
        }
        if (additionalConfigSource != null) {
            additionalConfigSource.applyAdditionalConfiguration(String.format("openpos.services.profiles.%s", profileId), config);
        }
        return config;
    }

}
