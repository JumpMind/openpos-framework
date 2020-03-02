package org.jumpmind.pos.core.flow.config;

import org.jumpmind.pos.core.flow.FlowException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YamlTransitionStepProvider {

    public List<YamlTransitionStepConfig> loadTransitionSteps(ResourcePatternResolver resolver, String appId, String path, YamlFlowConfigFileLoader flowConfigLoader) {
        try {
            Resource[] resources = resolver.getResources("classpath*:/" + path + "/*-steps.yml");
            if (resources == null || resources.length == 0) {
                throw new FlowException("Failed to load YML transition steps. pattern: " + "classpath*:/" + path + "/*-steps.yml");
            }
            return loadYamlTransitionStepConfigs(resources[0].getInputStream(), flowConfigLoader);
        } catch (Exception ex) {
            throw new FlowException(String.format("Failed to load YML transition steps for appId %s and path %s", appId, path), ex);
        }
    }

    public List<YamlTransitionStepConfig> loadYamlTransitionStepConfigs(InputStream inputStream, YamlFlowConfigFileLoader flowConfigLoader) {
        Yaml yaml = new Yaml();

        List<YamlTransitionStepConfig> yamlStepConfigs = new ArrayList<>();

        Map<String, Object> yamlDoc = (Map<String, Object>) yaml.load(inputStream);

        if (yamlDoc == null || yamlDoc.get("TransitionSteps") == null) {
            return null;
        }

        List<Object> transitionStepsRaw = (List<Object>) yamlDoc.get("TransitionSteps");

        for (Object transitionStepRaw : transitionStepsRaw) {
            YamlTransitionStepConfig yamlTransitionStepConfig = new YamlTransitionStepConfig();

            if (transitionStepRaw instanceof String) {
                yamlTransitionStepConfig.setTransitionStepName((String) transitionStepRaw);
            } else if (transitionStepRaw instanceof Map) {
                Map<String, Map<String, Object>> transitionStepMap = (Map<String, Map<String, Object>>) transitionStepRaw;
                String transitionStepName = transitionStepMap.keySet().iterator().next();

                yamlTransitionStepConfig.setTransitionStepName(transitionStepName);

                Map<String, Object> actionToFlowReference = transitionStepMap.get(transitionStepName);

                for (String actionName : actionToFlowReference.keySet()) {
                    Map<String, Object> flowReference = (Map<String, Object>) actionToFlowReference.get(actionName);
                    YamlStateConfig substateReference = flowConfigLoader.loadSubstateReference(actionName, flowReference);
                    yamlTransitionStepConfig.getActionToStateConfigs().put(actionName, substateReference);
                }
            } else {
                throw new FlowException("Don't know what this is: " + transitionStepRaw);
            }

            yamlStepConfigs.add(yamlTransitionStepConfig);
        }

        return yamlStepConfigs;
    }


}
