package org.jumpmind.pos.core.clientconfiguration;

import java.util.List;
import java.util.Map;

public interface IClientConfigSelector {
    Map<String, Map<String, String>> getConfigurations(Map<String, String> properties, List<String> additionalTags);
}
