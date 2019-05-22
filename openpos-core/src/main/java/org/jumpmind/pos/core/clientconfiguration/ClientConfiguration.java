package org.jumpmind.pos.core.clientconfiguration;

import java.io.Serializable;
import java.util.Map;

public class ClientConfiguration implements Serializable {
    private Map<String, String> properties;

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }
}
