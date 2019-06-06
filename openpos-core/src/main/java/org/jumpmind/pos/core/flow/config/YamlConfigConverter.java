package org.jumpmind.pos.core.flow.config;

import java.lang.reflect.Method;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jumpmind.pos.core.flow.CompleteState;
import org.jumpmind.pos.core.flow.FlowException;
import org.jumpmind.pos.core.flow.IState;
import org.jumpmind.pos.core.flow.OnArrive;
import org.jumpmind.pos.util.ClassUtils;


public class YamlConfigConverter {
    
    private static final Logger log = LogManager.getLogger(YamlConfigConverter.class);
    
    private static Map<String, Class<Object>> knownStateClasses;
    
    private final static String GLOBAL_CONFIG = "Global";

    private List<String> additionalPackages;

    public YamlConfigConverter() {
    }

    public YamlConfigConverter(List<String> additionalPackages) {
        this.additionalPackages = additionalPackages;
    }

    public FlowConfig convertToFlowConfig(List<YamlFlowConfig> loadedYamlFlowConfigs, YamlFlowConfig yamlFlowConfig) {
        FlowConfig flowConfig = converFlowConfig(loadedYamlFlowConfigs, yamlFlowConfig);
        return flowConfig;
    }
    
    public List<FlowConfig> convertToFlowConfig(List<YamlFlowConfig> yamlFlowConfigs) {
        
        List<FlowConfig> flowConfigs = new ArrayList<FlowConfig>();
        
        for (YamlFlowConfig yamlFlowConfig : yamlFlowConfigs) {            
            FlowConfig flowConfig = converFlowConfig(yamlFlowConfigs, yamlFlowConfig);
            flowConfigs.add(flowConfig);
        }
        
        return flowConfigs;
    }
    
    public List<FlowConfig> convertFlowConfigs(List<YamlFlowConfig> loadedYamlFlowConfigs, List<YamlFlowConfig> yamlFlowConfigs) {
        List<FlowConfig> flowConfigs = new ArrayList<FlowConfig>();
        for (YamlFlowConfig yamlFlowConfig : yamlFlowConfigs) {
            flowConfigs.add(converFlowConfig(loadedYamlFlowConfigs, yamlFlowConfig));
        }
        
        return flowConfigs;
    }

    private FlowConfig converFlowConfig(List<YamlFlowConfig> yamlFlowConfigs, YamlFlowConfig yamlFlowConfig) {
        FlowConfig flowConfig = new FlowConfig(yamlFlowConfig.getFlowName());
        
        Map<String, YamlStateConfig> concreteStateConfigs = getConcreteStateConfigs(yamlFlowConfig);
        
        boolean initialState = true;
        
        for (String stateName : concreteStateConfigs.keySet()) {
            if (GLOBAL_CONFIG.equalsIgnoreCase(stateName)) {
                handleGlobalConfig(flowConfig, yamlFlowConfigs, concreteStateConfigs.get(stateName));
                continue;
            }
            
            StateConfig stateConfig = buildStateConfig(yamlFlowConfigs, concreteStateConfigs.get(stateName));
            if (initialState) {
                flowConfig.setInitialState(stateConfig);
                initialState = false;
            }
            flowConfig.add(stateConfig);
        }
        
        return flowConfig;
    }
    
    protected StateConfig buildStateConfig(List<YamlFlowConfig> yamlFlowConfigs, YamlStateConfig yamlStateConfig) {
        StateConfig stateConfig = new StateConfig();
        stateConfig.setStateName(yamlStateConfig.getStateName());
        
        Class<? extends Object> stateClass = resolveStateClass(yamlStateConfig);
        
        if (stateClass != null) {
            stateConfig.setStateClass(resolveStateClass(yamlStateConfig));            
        } else {
            throw new FlowException("Failed to resolve state class for name from yaml: " + yamlStateConfig.getStateName());
        }
        
        stateConfig.setActionToStateMapping(buildActionToStateMapping(yamlFlowConfigs, yamlStateConfig));
        stateConfig.setActionToSubStateMapping(buildActionToSubStateMapping(yamlFlowConfigs, yamlStateConfig));
        return stateConfig;
    }

    protected void handleGlobalConfig(FlowConfig flowConfig, List<YamlFlowConfig> yamlFlowConfigs, YamlStateConfig yamlStateConfig) {
        Map<String, Class<? extends Object>> actionToStateMapping = buildActionToStateMapping(yamlFlowConfigs, yamlStateConfig);
        for (String actionName : actionToStateMapping.keySet()) {
            flowConfig.addGlobalTransition(actionName, actionToStateMapping.get(actionName));
        }
        
        Map<String, SubTransition> actionToSubStateMapping = buildActionToSubStateMapping(yamlFlowConfigs, yamlStateConfig);
        for (String actionName : actionToSubStateMapping.keySet()) {
            FlowConfig subFlowConfig = actionToSubStateMapping.get(actionName).getSubFlowConfig();
            flowConfig.addGlobalSubTransition(actionName, subFlowConfig);
        }
    }

    protected Map<String, Class<? extends Object>> buildActionToStateMapping(List<YamlFlowConfig> yamlFlowConfigs, YamlStateConfig yamlStateConfig) {
        Map<String, Class<? extends Object>> actionToStateMapping = new LinkedHashMap<>();
        
        for (String actionName : yamlStateConfig.getActionToStateConfigs().keySet()) {
            YamlStateConfig stateConfig = yamlStateConfig.getActionToStateConfigs().get(actionName);
            if (!stateConfig.isSubTransition()) {
                Class<? extends Object> stateClass = resolveStateClass(stateConfig);
                
                if (stateClass != null) {                    
                    actionToStateMapping.put(actionName, stateClass);
                } else {                    
                    throw new FlowException("Failed to resolve state class for name from yaml: \"" + yamlStateConfig.getStateName() + "\"");
                }
            }
        }
        
        return actionToStateMapping;
    }
    
    @SuppressWarnings("all")
    protected Map<String, SubTransition> buildActionToSubStateMapping(List<YamlFlowConfig> yamlFlowConfigs, YamlStateConfig yamlStateConfig) {
        Map<String, SubTransition> actionToSubStateMapping = new LinkedHashMap<>();
        
        for (String actionName : yamlStateConfig.getActionToStateConfigs().keySet()) {
            YamlStateConfig stateConfig = yamlStateConfig.getActionToStateConfigs().get(actionName);
            if (stateConfig.isSubTransition()) {
                SubTransition subTransition = new SubTransition();
                
                YamlFlowConfig yamlFlowConfig = findFlowByName(stateConfig.getStateName(), yamlFlowConfigs);
                if (yamlFlowConfig != null) {
                    FlowConfig flowConfig = convertToFlowConfig(yamlFlowConfigs, yamlFlowConfig);
                    subTransition.setSubFlowConfig(flowConfig);
                } else {
                    Class<? extends Object> stateClass = resolveStateClass(stateConfig);
                    if (stateClass == null) {
                        throw new FlowException("Failed to resolve substate reference to a subflow or a state class: " + 
                                stateConfig.getStateName() + " referred to by action: " + actionName);
                    }
                    FlowConfig flowConfig = new FlowConfig(stateClass.getSimpleName());
                    FlowBuilder builder = FlowBuilder.addState(stateClass);
                    for (String returnAction : stateConfig.getReturnActions()) {
                        builder.withTransition(returnAction, CompleteState.class);
                    }
                    flowConfig.setInitialState(builder.build());
                    subTransition.setSubFlowConfig(flowConfig);
                }
                
                subTransition.setReturnActionNames(stateConfig.getReturnActions().toArray(new String[] {}));
                
                subTransition.getSubFlowConfig().setConfigScope((Map)stateConfig.getConfigScope());
                actionToSubStateMapping.put(actionName, subTransition);
            }
        }
        
        return actionToSubStateMapping;
    }

    protected YamlFlowConfig findFlowByName(String stateName, List<YamlFlowConfig> yamlFlowConfigs) {
        return yamlFlowConfigs.stream()
                .filter(yamlFlowConfig -> stateName.equals(yamlFlowConfig.getFlowName())).findAny().orElse(null);
    }

    @SuppressWarnings("rawtypes")
    protected Class<? extends Object> resolveStateClass(YamlStateConfig yamlStateConfig) {
        
        if (knownStateClasses == null) {

            List<Class<Object>> knownStateClassList = ClassUtils.getClassesForPackageAndType("org.jumpmind.pos", Object.class);
            if(additionalPackages != null) {
                additionalPackages.forEach( p -> knownStateClassList.addAll( ClassUtils.getClassesForPackageAndType( p, Object.class)));
            }

            knownStateClasses = knownStateClassList.stream()
                    .filter(clazz -> FlowUtil.isState(clazz))
                    .collect(Collectors.toMap(e -> ((Class)e).getSimpleName(), v -> v) );
        }
        
        Class<Object> state = knownStateClasses.get(yamlStateConfig.getStateName());
        
        if (state != null) {
            return state;
        } else {
            return null;
        }
    }

    protected Map<String, YamlStateConfig> getConcreteStateConfigs(YamlFlowConfig yamlFlowConfig) {

        Map<String, YamlStateConfig> concreteStateConfigs = new LinkedHashMap<String, YamlStateConfig>();

        for (YamlStateConfig yamlStateConfig : yamlFlowConfig.getFlowStateConfigs()) {            
            getConcreteStateConfigs(concreteStateConfigs, yamlStateConfig);
        }
        
        return concreteStateConfigs;
    }

    protected void getConcreteStateConfigs(Map<String, YamlStateConfig> concreteStateConfigs, 
            YamlStateConfig yamlStateConfig) {
        if (yamlStateConfig.isConcreteStateDefinition()) {
            if (!concreteStateConfigs.containsKey(yamlStateConfig.getStateName())) {
                concreteStateConfigs.put(yamlStateConfig.getStateName(), yamlStateConfig);
                for (YamlStateConfig targetState : yamlStateConfig.getActionToStateConfigs().values()) {
                    getConcreteStateConfigs(concreteStateConfigs, targetState);
                }
            } else {
                if (!concreteStateConfigs.containsValue(yamlStateConfig)) {                    
                    throw new FlowException(String.format("State \"%s\"is defined conceretely (with actions) more than once. This is not currently supported. ", yamlStateConfig.getStateName()));
                }
            }
        }
    }

}
