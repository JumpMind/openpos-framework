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
import java.util.concurrent.ConcurrentHashMap;

import org.jumpmind.pos.core.flow.config.IFlowConfigProvider;
import org.jumpmind.pos.core.service.IScreenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("singleton")
public class StateManagerContainer implements IStateManagerContainer {

    @Autowired
    IFlowConfigProvider flowConfigProvider;

    @Autowired
    IScreenService screenService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired(required=false)
    IErrorHandler errorHandler;

    private Map<String, Map<String, StateManager>> stateManagersByAppIdByNodeId = new HashMap<>();

    private ThreadLocal<IStateManager> currentStateManager = new InheritableThreadLocal<>();

    @Override
    public synchronized void removeSessionIdVariables(String sessionId) {
        for (Map<String, StateManager> map : stateManagersByAppIdByNodeId.values()) {
            for (StateManager stateManager : map.values()) {
                stateManager.removeSessionAuthentication(sessionId);
                stateManager.removeSessionCompatible(sessionId);
            }
        }
    }

    @Override
    public synchronized IStateManager retrieve(String appId, String deviceId) {
        Map<String, StateManager> stateManagersByNodeId = stateManagersByAppIdByNodeId.get(appId);
        if (stateManagersByNodeId != null) {
            IStateManager stateManager = stateManagersByNodeId.get(deviceId);
            setCurrentStateManager(stateManager);
            return stateManager;
        } else {
            return null;
        }
    }

    @Override
    public IStateManager create(String appId, String deviceId, Map<String, Object> queryParams, Map<String, String> personalizationProperties) {
        
        Map<String, StateManager> stateManagersByNodeId;
        synchronized (this) {            
            stateManagersByNodeId = stateManagersByAppIdByNodeId.get(appId);
            if (stateManagersByNodeId == null) {
                if (stateManagersByNodeId == null) {
                    stateManagersByNodeId = new ConcurrentHashMap<>();
                    stateManagersByAppIdByNodeId.put(appId, stateManagersByNodeId);
                }
            }
        }

        StateManager stateManager = stateManagersByNodeId.get(deviceId);
        if (stateManager == null) {
            if (stateManager == null) {
                stateManager = applicationContext.getBean(StateManager.class);
                setCurrentStateManager(stateManager);
                stateManager.registerQueryParams(queryParams);
                stateManager.registerPersonalizationProperties(personalizationProperties);
                stateManager.setErrorHandler(errorHandler);
                stateManager.setInitialFlowConfig(flowConfigProvider.getConfig(appId, deviceId));
                stateManagersByNodeId.put(deviceId, stateManager);
                stateManager.init(appId, deviceId);
            }
        }
        return stateManager;
    }

    @Override
    public synchronized void remove(String appId, String deviceId) {
        Map<String, StateManager> stateManagersByNodeId = stateManagersByAppIdByNodeId.get(appId);
        if (stateManagersByNodeId != null) {
            stateManagersByNodeId.remove(deviceId);
        }
    }

    public synchronized List<StateManager> getAllStateManagers() {
        List<StateManager> allStateManagers = new ArrayList<>();

        for (Map<String, StateManager> stateManagersByNodeId : stateManagersByAppIdByNodeId.values()) {
            for (StateManager stateManager : stateManagersByNodeId.values()) {
                allStateManagers.add(stateManager);
            }
        }

        return allStateManagers;
    }

    public void setCurrentStateManager(IStateManager stateManager) {
        currentStateManager.set(stateManager);
    }

    public IStateManager getCurrentStateManager() {
        return currentStateManager.get();        
    }

}
