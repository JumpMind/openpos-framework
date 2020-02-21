package org.jumpmind.pos.devices.service.model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openpos.ui.personalization")
public class PersonalizationParameters {

    private String devicePattern;

    private List<PersonalizationParameter> parameters = new ArrayList<>();

    public List<PersonalizationParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<PersonalizationParameter> parameters) {
        this.parameters = parameters;
    }

    public String getDevicePattern() {
        return devicePattern;
    }

    public void setDevicePattern(String devicePattern) {
        this.devicePattern = devicePattern;
    }

}
