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
package org.jumpmind.pos.core.flow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.core.flow.config.IFlowConfigProvider;
import org.jumpmind.pos.core.service.IScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class StateManagerFactory implements IStateManagerFactory {

    @Autowired
    IFlowConfigProvider flowConfigProvider;

    @Autowired
    IScreenService screenService;

    @Autowired
    ApplicationContext applicationContext;

    private Map<String, Map<String, StateManager>> stateManagersByAppIdByNodeId = new HashMap<>();

    @Override
    public IStateManager retrieve(String appId, String nodeId) {
        Map<String, StateManager> stateManagersByNodeId = stateManagersByAppIdByNodeId.get(appId);
        if (stateManagersByNodeId != null) {
            return stateManagersByNodeId.get(nodeId);
        } else {
            return null;
        }
    }

    @Override
    public IStateManager create(String appId, String nodeId) {
        Map<String, StateManager> stateManagersByNodeId = stateManagersByAppIdByNodeId.get(appId);
        if (stateManagersByNodeId == null) {
            synchronized (this) {
                if (stateManagersByNodeId == null) {
                    stateManagersByNodeId = new HashMap<>();
                    stateManagersByAppIdByNodeId.put(appId, stateManagersByNodeId);
                }
            }
        }

        StateManager stateManager = stateManagersByNodeId.get(nodeId);
        if (stateManager == null) {
            synchronized (this) {
                if (stateManager == null) {
                    stateManager = applicationContext.getBean(StateManager.class);
                    stateManager.setInitialFlowConfig(flowConfigProvider.getConfig(appId, nodeId));
                    stateManager.init(appId, nodeId);
                    stateManagersByNodeId.put(nodeId, stateManager);
                }
            }
        }
        return stateManager;
    }
    
    public List<StateManager> getAllStateManagers() {
        List<StateManager> allStateManagers = new ArrayList<>();
        
        for (Map<String, StateManager> stateManagersByNodeId : stateManagersByAppIdByNodeId.values()) {
            for (StateManager stateManager : stateManagersByNodeId.values()) {
                allStateManagers.add(stateManager);
            }
        }
        
        return allStateManagers;
    }

}
