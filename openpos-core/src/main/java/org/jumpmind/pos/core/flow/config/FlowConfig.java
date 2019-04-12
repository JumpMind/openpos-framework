/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;


public class FlowConfig {
    
    private String name;
    @JsonIgnore
    private StateConfig initialState;
    @JsonIgnore
    private Map<Class<? extends Object>, StateConfig> stateConfigs = new HashMap<>();
    @JsonIgnore
    private Map<String, Object> configScope = new HashMap<>();
    @JsonIgnore
    private Map<String, Class<? extends Object>> actionToStateMapping = new HashMap<>();
    @JsonIgnore
    private Map<String, SubTransition> actionToSubStateMapping = new HashMap<>();
    
    public FlowConfig() {
        
    }
    public FlowConfig(String name) {
        this.name = name;
    }
    
    public FlowConfig(String name, Map<String, Object> configScope) {
        this(name);
        this.configScope = configScope;
    }
    
    public StateConfig getStateConfig(Object state) {
        return stateConfigs.get(state.getClass());
    }
    
    public StateConfig getStateConfig(Class<? extends Object> stateClass) {
        return stateConfigs.get(stateClass);
    }
    
    public void add(StateConfig config) {
        stateConfigs.put(config.getStateClass(), config);
        autoConfigureTargetStates(config);
    }
    
    protected void autoConfigureTargetStates(StateConfig config) {
        Collection<Class<? extends Object>> targetStateClasses = 
                config.getActionToStateMapping().values();
        
        for (Class<? extends Object> targetStateClass : targetStateClasses) {
            autoConfigureTargetState(targetStateClass);
        }
    }
    
    protected void autoConfigureTargetState(Class<? extends Object> targetStateClass) {
        if (!stateConfigs.containsKey(targetStateClass)) {
            StateConfig stateConfig = new StateConfig();
            stateConfig.setStateName(FlowUtil.getStateName(targetStateClass));
            stateConfig.setStateClass(targetStateClass);                
            stateConfigs.put(targetStateClass, stateConfig);
        }        
    }

    public StateConfig getInitialState() {
        return initialState;
    }

    public FlowConfig setInitialState(StateConfig initialState) {
        add(initialState);
        this.initialState = initialState;
        return this;
    }

    public Map<String, Object> getConfigScope() {
        return configScope;
    }

    public void setConfigScope(Map<String, Object> configScope) {
        this.configScope = configScope;
    }
    
    public void addGlobalTransition(String actionName, Class<? extends Object> destination) {
        actionToStateMapping.put(actionName, destination);
        autoConfigureTargetState(destination);
    }

    public void addGlobalSubTransition(String string, FlowConfig flowConfig) {
        SubTransition subTransition = new SubTransition(null, flowConfig);
        actionToSubStateMapping.put(string, subTransition);
    }

    public Map<Class<? extends Object>, StateConfig> getStateConfigs() {
        return stateConfigs;
    }

    public void setStateConfigs(Map<Class<? extends Object>, StateConfig> stateConfigs) {
        this.stateConfigs = stateConfigs;
    }

    public Map<String, Class<? extends Object>> getActionToStateMapping() {
        return actionToStateMapping;
    }

    public void setActionToStateMapping(Map<String, Class<? extends Object>> actionToStateMapping) {
        this.actionToStateMapping = actionToStateMapping;
    }

    public Map<String, SubTransition> getActionToSubStateMapping() {
        return actionToSubStateMapping;
    }

    public void setActionToSubStateMapping(Map<String, SubTransition> actionToSubStateMapping) {
        this.actionToSubStateMapping = actionToSubStateMapping;
    }

    public String getName() {
        if (StringUtils.isEmpty(name)) {
            String className = this.getClass().getSimpleName();
            final String CONFIG_SUFFIX = "Config";
            if (className.endsWith("Config")) {
                name = className.substring(0, className.length()-CONFIG_SUFFIX.length());
            } else {
                name = className;
            }
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
