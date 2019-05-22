package org.jumpmind.pos.core.clientconfiguration;

import java.util.Map;

public interface IClientConfigSelector {
    Map<String, Map<String, String>> getConfigurations();
}
