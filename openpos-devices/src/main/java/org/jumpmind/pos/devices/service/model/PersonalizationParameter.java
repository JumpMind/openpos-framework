package org.jumpmind.pos.devices.service.model;

import java.io.Serializable;

public class PersonalizationParameter implements Serializable {
    private static final long serialVersionUID = 1L;

    private String property;
    private String label;
    private String defaultValue;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
}