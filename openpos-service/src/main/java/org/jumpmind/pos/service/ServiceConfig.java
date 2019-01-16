package org.jumpmind.pos.service;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openpos.services")
public class ServiceConfig {

    public static String LOCAL_PROFILE = "local";

    protected Map<String, ServiceCommonConfig> commonConfig;
    protected Map<String, ServiceSpecificConfig> specificConfig;

    @Autowired(required = false)
    IConfigApplicator additionalConfigSource;

    public Map<String, ServiceCommonConfig> getCommonConfig() {
        if (commonConfig == null) {
            commonConfig = new HashMap<>();
        }
        return commonConfig;
    }

    public void setCommonConfig(Map<String, ServiceCommonConfig> commonConfig) {
        this.commonConfig = commonConfig;
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

    public ServiceSpecificConfig getServiceConfig(String deviceId, String serviceId) {
        ServiceSpecificConfig config = getSpecificConfig().get(serviceId);
        if (config == null) {
            config = new ServiceSpecificConfig();
        } else {
            config = config.copy();
        }

        if (!ServiceConfig.LOCAL_PROFILE.equals(config.getProfile())) {
            ServiceCommonConfig commonConfig = getCommonConfig().get(config.getProfile());
            if (commonConfig != null) {
                if (config.getHttpTimeout() <= 0) {
                    config.setHttpTimeout(commonConfig.getHttpTimeout());
                }
                if (isBlank(config.getUrl())) {
                    config.setUrl(commonConfig.getUrl());
                }
            }
        }

        if (additionalConfigSource != null) {
            additionalConfigSource.applyAdditionalConfiguration(deviceId, String.format("openpos.services.specificConfig.%s", serviceId), config);
        }

        return config;
    }

}
