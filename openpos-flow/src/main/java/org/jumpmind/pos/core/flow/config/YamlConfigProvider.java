package org.jumpmind.pos.core.flow.config;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jumpmind.pos.core.flow.FlowException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class YamlConfigProvider implements IFlowConfigProvider {

    private static final Logger log = Logger.getLogger(YamlConfigProvider.class);

    private YamlFlowConfigFileLoader flowConfigLoader = new YamlFlowConfigFileLoader();
    private YamlConfigConverter flowConfigConverter;

    protected Map<String, List<FlowConfig>> loadedFlowConfigs = new LinkedHashMap<String, List<FlowConfig>>();
    protected Map<String, List<YamlFlowConfig>> loadedYamlFlowConfigs = new HashMap<String, List<YamlFlowConfig>>();
    protected Map<String, String> appIdToStartFlowName = new HashMap<>();

    public YamlConfigProvider() {
        this(null);
    }

    public YamlConfigProvider(List<String> additionalPackages) {
        flowConfigConverter = new YamlConfigConverter(additionalPackages);
    }

    public void load(String appId, String path, String startFlowName) {
        appIdToStartFlowName.put(appId, startFlowName);
        load(appId, path);
    }

    public void load(String appId, String path) {
        try {
            ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
            Resource[] resources = resolver.getResources("classpath*:/" + path + "/*.yml");

            List<YamlFlowConfig> yamlFlowConfigs = new ArrayList<>();

            for (Resource resource : resources) {
                // first pass needs to load all raw YAML.
                yamlFlowConfigs.addAll(loadYamlResource(appId, path, resource));
            }

            // second pass here needs to then convert
            loadYamlFlowConfigs(appId, yamlFlowConfigs);

        } catch (Exception ex) {
            throw new FlowException(String.format("Failed to load YML flow config for appId %s and path %s", appId, path), ex);
        }
    }
    
    public void load(String appId, InputStream resource) {
        List<YamlFlowConfig> yamlFlowConfigs = new ArrayList<>();
        yamlFlowConfigs.addAll(loadYamlResource(appId, resource));
        // second pass here needs to then convert
        loadYamlFlowConfigs(appId, yamlFlowConfigs);
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
    
    protected List<YamlFlowConfig> loadYamlResource(String appId, InputStream resource) {      
        List<YamlFlowConfig> yamlFlowConfigs = flowConfigLoader.loadYamlFlowConfigs(resource);
        List<YamlFlowConfig> existingYamlFlowConfigs = loadedYamlFlowConfigs.get(appId);
        
        if (existingYamlFlowConfigs == null) {
            existingYamlFlowConfigs = new ArrayList<YamlFlowConfig>();
            loadedYamlFlowConfigs.put(appId, existingYamlFlowConfigs);
        }
        existingYamlFlowConfigs.addAll(yamlFlowConfigs);
        return existingYamlFlowConfigs;
    }

    protected List<YamlFlowConfig> loadYamlResource(String appId, String path, Resource resource) {
        log.info("Loading flow config from " + resource);
        try {
            return loadYamlResource(appId, resource.getInputStream());
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

    }

}
