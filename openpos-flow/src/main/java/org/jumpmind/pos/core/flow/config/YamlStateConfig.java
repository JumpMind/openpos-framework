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
 * http://www.gnu.org/licenses.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.core.flow.FlowException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class YamlStateConfig {
    
    private String stateName;
    private Map<String, YamlStateConfig> actionToStateConfigs = new HashMap<>();
    
    private boolean isSubTransition;
    private List<String> returnActions = new ArrayList<>();
    
    private Map<String, String> configScope = new HashMap<>();
    
    public YamlStateConfig(String stateName) {
        this.stateName = stateName;
    }
    
    public boolean isConcreteStateDefinition() {
        return !actionToStateConfigs.isEmpty();
    }

    @Override
    public String toString() {
        return "YamlStateConfig [stateName=" + stateName + ", actionToStateConfigs=" + actionToStateConfigs + ", isSubTransition="
                + isSubTransition + ", returnActions=" + returnActions + "]";
    } 

    public void merge( YamlStateConfig stateConfig){
        stateConfig.getActionToStateConfigs().forEach((s, stateConfig1) -> {
            if(actionToStateConfigs.containsKey(s)){
                log.info("Overriding transition {} on state {}", s, stateName );
            }
            actionToStateConfigs.put(s, stateConfig1);
        });
    }

}
