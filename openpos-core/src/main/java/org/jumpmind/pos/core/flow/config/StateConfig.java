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

import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.core.flow.IState;


public class StateConfig {
    
    private String stateName;
    private Class<? extends IState> stateClass;
    private Map<String, Class<? extends IState>> actionToStateMapping = new HashMap<>();
    private Map<String, FlowConfig> actionToSubStateMapping = new HashMap<>();

    public String getStateName() {
        return stateName;
    }
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
    public Class<? extends IState> getStateClass() {
        return stateClass;
    }
    public void setStateClass(Class<? extends IState> stateClass) {
        this.stateClass = stateClass;
    }
    public Map<String, Class<? extends IState>> getActionToStateMapping() {
        return actionToStateMapping;
    }
    public void setActionToStateMapping(Map<String, Class<? extends IState>> actionToStateMapping) {
        this.actionToStateMapping = actionToStateMapping;
    }
    public Map<String, FlowConfig> getActionToSubStateMapping() {
        return actionToSubStateMapping;
    }
    public void setActionToSubStateMapping(Map<String, FlowConfig> actionToSubStateMapping) {
        this.actionToSubStateMapping = actionToSubStateMapping;
    }    


}
