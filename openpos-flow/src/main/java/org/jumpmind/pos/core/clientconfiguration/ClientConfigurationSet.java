package org.jumpmind.pos.core.clientconfiguration;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class ClientConfigurationSet implements Serializable {
    private List<String> tags;
    private Map<String, Map<String, String>> configsForTags;

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Map<String, Map<String, String>> getConfigsForTags() {
        return configsForTags;
    }

    public void setConfigsForTags(Map<String, Map<String, String>> configsForTags) {
        this.configsForTags = configsForTags;
    }
}
