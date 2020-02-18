package org.jumpmind.pos.core.flow.config;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class YamlTransitionStepConfig {

    private String transitionStepName;
    private Map<String, YamlStateConfig> actionToStateConfigs = new HashMap<>();
}
