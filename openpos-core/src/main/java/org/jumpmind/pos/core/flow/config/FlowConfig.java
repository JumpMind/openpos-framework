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

import org.jumpmind.pos.core.flow.IState;


public class FlowConfig {
    
    private StateConfig initialState;
    private Map<Class<? extends IState>, StateConfig> stateConfigs = new HashMap<>();
    private String returnAction;
    private Map<String, Object> configScope = new HashMap<>();
    
    public FlowConfig() {
    }
    
    public FlowConfig(Map<String, Object> configScope) {
        this.configScope = configScope;
    }
    
    public StateConfig getStateConfig(IState state) {
        return stateConfigs.get(state.getClass());
    }
    
    public StateConfig getStateConfig(Class<? extends IState> stateClass) {
        return stateConfigs.get(stateClass);
    }
    
    public void add(StateConfig config) {
        stateConfigs.put(config.getStateClass(), config);
        autoConfigureTargetStates(config);
    }
    
    protected void autoConfigureTargetStates(StateConfig config) {
        Collection<Class<? extends IState>> targetStateClasses = 
                config.getActionToStateMapping().values();
        
        for (Class<? extends IState> targetStateClass : targetStateClasses) {
            if (!stateConfigs.containsKey(targetStateClass)) {
                StateConfig stateConfig = new StateConfig();
                stateConfig.setStateName(FlowUtil.getStateName(targetStateClass));
                stateConfig.setStateClass(targetStateClass);                
                stateConfigs.put(targetStateClass, stateConfig);
            }
        }
    }

    public StateConfig getInitialState() {
        return initialState;
    }

    public void setInitialState(StateConfig initialState) {
        add(initialState);
        this.initialState = initialState;
    }

    public String getReturnAction() {
        return returnAction;
    }

    public void setReturnAction(String returnAction) {
        this.returnAction = returnAction;
    }

    public Map<String, Object> getConfigScope() {
        return configScope;
    }

    public void setConfigScope(Map<String, Object> configScope) {
        this.configScope = configScope;
    }


}
