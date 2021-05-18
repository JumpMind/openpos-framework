package org.jumpmind.pos.core.flow.config;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.core.flow.FlowException;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.InputStream;
import java.util.*;

@Slf4j
public class YamlConfigProvider implements IFlowConfigProvider {

    private YamlFlowConfigFileLoader flowConfigLoader = new YamlFlowConfigFileLoader();
    private YamlConfigConverter flowConfigConverter;

    protected Map<String, List<FlowConfig>> loadedFlowConfigs = new LinkedHashMap<String, List<FlowConfig>>();
    protected Map<String, List<YamlFlowConfig>> loadedYamlFlowConfigs = new HashMap<String, List<YamlFlowConfig>>();
    protected Map<String, String> appIdToStartFlowName = new HashMap<>();

    private Map<String, List<TransitionStepConfig>> transitionSteps = new HashMap<>();

    public YamlConfigProvider() {
        this(null);
    }

    public YamlConfigProvider(List<String> additionalPackages) {
        flowConfigConverter = new YamlConfigConverter(additionalPackages);
    }

    @Override
    public void load(String appId, String path, String startFlowName) {
        appIdToStartFlowName.put(appId, startFlowName);
        load(appId, path);
    }

    @Bean
    public List<String> loadedAppIds() {
        return new ArrayList<>(appIdToStartFlowName.keySet());
    }

    public void load(String appId, String path) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());

            List<Resource> resources = new ArrayList<>(Arrays.asList(resolver.getResources("classpath*:/" + path + "/*-flow-ext.yml")));
            resources.addAll(Arrays.asList(resolver.getResources("classpath*:/" + path + "/*-flow.yml")));

            List<YamlFlowConfig> yamlFlowConfigs = new ArrayList<>();

            for (int i = resources.size() - 1; i >= 0; --i) {
                Resource resource = resources.get(i);
                // first pass needs to load all raw YAML.
                yamlFlowConfigs.addAll(loadYamlResource(appId, path, resource));
            }

            // second pass here needed to convert
            loadYamlFlowConfigs(appId, yamlFlowConfigs);

            transitionSteps.put(appId, flowConfigConverter.convertTransitionSteps(new YamlTransitionStepProvider().loadTransitionSteps(resolver, appId, path, flowConfigLoader), yamlFlowConfigs));

        } catch (Exception ex) {
            throw new FlowException(String.format("Failed to load YML flow config for appId '%s' and path '%s'", appId, path), ex);
        }
    }

    public void load(String appId, InputStream resource) {
        List<YamlFlowConfig> yamlFlowConfigs = new ArrayList<>();
        yamlFlowConfigs.addAll(loadYamlResource(appId, resource, false));
        // second pass here needs to then convert
        loadYamlFlowConfigs(appId, yamlFlowConfigs);
    }

    @Override
    public List<TransitionStepConfig> getTransitionStepConfig(String appId, String nodeId) {
        return transitionSteps.get(appId);
    }

    @Override
    public FlowConfig getConfigByName(String appId, String deviceId, String name) {
        List<FlowConfig> flowConfigs = loadedFlowConfigs.get(appId);
        if (flowConfigs != null && !flowConfigs.isEmpty()) {
            return flowConfigs.stream().filter(flowConfig -> name.equals(flowConfig.getName())).findAny().orElse(null);
        } else {
            return null;
        }
    }

    @Override
    public FlowConfig getConfig(String appId, String deviceId) {
        if (appIdToStartFlowName.containsKey(appId)) {
            return getConfigByName(appId, deviceId, appIdToStartFlowName.get(appId));
        } else {
            List<FlowConfig> flowConfigs = loadedFlowConfigs.get(appId);
            if (flowConfigs != null && !flowConfigs.isEmpty()) {
                return flowConfigs.get(0);
            } else {
                return null;
            }
        }
    }

    protected void updateSubflowsWithParentConfigScope(FlowConfig flowConfig) {
        updateSubflowsWithParentConfigScope(flowConfig, flowConfig.getConfigScope());
    }

    protected void updateSubflowsWithParentConfigScope(FlowConfig flowConfig, Map<String, Object> inheritedConfigScope) {
        flowConfig.getStateConfigs().values().forEach(sc -> {
            updateSubflowsWithParentConfigScope(flowConfig, sc.getActionToSubStateMapping().values(), inheritedConfigScope);
        });
        updateSubflowsWithParentConfigScope(flowConfig, flowConfig.getActionToSubStateMapping().values(), inheritedConfigScope);
    }

    protected void updateSubflowsWithParentConfigScope(FlowConfig flowConfig, Collection<SubFlowConfig> configs, Map<String, Object> inheritedConfigScope) {
        configs.forEach(sf -> {
            Map<String, Object> currentScope = new HashMap<>(inheritedConfigScope);
            currentScope.putAll(sf.getSubFlowConfig().getConfigScope());
            sf.getSubFlowConfig().setConfigScope(currentScope);
            updateSubflowsWithParentConfigScope(sf.getSubFlowConfig(), currentScope);
        });
    }

    protected List<YamlFlowConfig> loadYamlResource(String appId, InputStream resource, boolean isExtension) {
        List<YamlFlowConfig> yamlFlowConfigs = flowConfigLoader.loadYamlFlowConfigs(resource);
        List<YamlFlowConfig> existingYamlFlowConfigs = loadedYamlFlowConfigs.computeIfAbsent(appId, k -> new ArrayList<>());

        for (YamlFlowConfig flowConfig : yamlFlowConfigs) {
            YamlFlowConfig match = existingYamlFlowConfigs.stream().filter(flowConfig1 -> flowConfig.getFlowName().equals(flowConfig1.getFlowName())).findFirst().orElse(null);
            if (isExtension) {
                handleExtensionConfig(flowConfig, match);
            } else {
                handleParentConfig(existingYamlFlowConfigs, flowConfig, match);
            }
        }

        return existingYamlFlowConfigs;
    }

    private void handleParentConfig(List<YamlFlowConfig> existingYamlFlowConfigs, YamlFlowConfig flowConfig, YamlFlowConfig match) {
        if (match == null) {
            existingYamlFlowConfigs.add(flowConfig);
        } else {
            throw new FlowException("Tried to load a parent flow, but there is one that already exists: %s", match.getFlowName());
        }
    }

    private void handleExtensionConfig(YamlFlowConfig flowConfig, YamlFlowConfig match) {
        if (match == null) {
            throw new FlowException("Tried to load an extension flow, but could not find a parent flow to extend: %s", flowConfig.getFlowName());
        } else {
            match.merge(flowConfig);
        }
    }

    protected List<YamlFlowConfig> loadYamlResource(String appId, String path, Resource resource) {
        log.info("Loading flow config from {} for {}", resource.toString(), appId);
        try {
            return loadYamlResource(appId, resource.getInputStream(), resource.getFilename().endsWith("-flow-ext.yml"));
        } catch (Exception ex) {
            throw new FlowException(String.format("Failed while loading resource %s", resource), ex);
        }
    }

    protected void loadYamlFlowConfigs(String appId, List<YamlFlowConfig> yamlFlowConfigs) {

        List<YamlFlowConfig> existingYamlFlowConfigs = loadedYamlFlowConfigs.get(appId);

        List<FlowConfig> existingFlowConfigs = loadedFlowConfigs.get(appId);
        if (existingFlowConfigs == null) {
            existingFlowConfigs = new ArrayList<FlowConfig>();
            loadedYamlFlowConfigs.put(appId, existingYamlFlowConfigs);
            loadedFlowConfigs.put(appId, existingFlowConfigs);
        }

        List<FlowConfig> flowConfigs = flowConfigConverter.convertFlowConfigs(existingYamlFlowConfigs, yamlFlowConfigs);
        existingFlowConfigs.addAll(flowConfigs);

        // inherit config scope down through child sub flows
        existingFlowConfigs.forEach(f -> updateSubflowsWithParentConfigScope(f));

    }

}
