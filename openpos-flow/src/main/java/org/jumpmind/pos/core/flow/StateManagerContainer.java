/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 * <p>
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.pos.core.flow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.core.flow.config.IFlowConfigProvider;
import org.jumpmind.pos.core.service.IScreenService;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.Event;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
public class StateManagerContainer implements IStateManagerContainer, ApplicationListener<Event> {

    @Autowired
    IFlowConfigProvider flowConfigProvider;

    @Autowired
    IScreenService screenService;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired(required = false)
    IErrorHandler errorHandler;

    @Autowired
    private ClientContext clientContext;

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


                stateManager.setTransitionSteps(createTransitionSteps());
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

    private List<ITransitionStep> createTransitionSteps() {
        List<ITransitionStep> steps = new ArrayList<>();
        String[] names = applicationContext.getBeanNamesForType(ITransitionStep.class);
        for (String name : names) {
            steps.add((ITransitionStep) applicationContext.getBean(name));
        }

        Collections.sort(steps, (o1, o2) -> {
            Integer o1order = 0;
            Integer o2order = 0;
            try {
                o1order = o1.getClass().getAnnotation(Order.class).value();
            } catch (NullPointerException ex) {
            }
            try {
                o2order = o2.getClass().getAnnotation(Order.class).value();
            } catch (NullPointerException ex) {
            }

            return o1order.compareTo(o2order);
        });
        return steps;
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
        if (stateManager != null && stateManager.getClientContext() != null) {
            for (String property : stateManager.getClientContext().keySet()) {
                clientContext.put(property, stateManager.getClientContext().get(property));
            }
        }
    }

    public IStateManager getCurrentStateManager() {
        return currentStateManager.get();
    }

    @Override
    public void onApplicationEvent(Event event) {
        for (Map<String, StateManager> map : new ArrayList<>(stateManagersByAppIdByNodeId.values())) {
            for (StateManager stateManager : new ArrayList<>(map.values())) {
                try {
                    if (process(stateManager, event)) {
                        setCurrentStateManager(stateManager);
                        stateManager.onEvent(event);
                    }
                } finally {
                    setCurrentStateManager(null);
                }
            }
        }
    }

    /**
     * Only process AppEvents that are from other state managers.  Wait to process the event until the current StateManager is idle.
     */
    private boolean process(IStateManager stateManager, Event event) {
        if (event instanceof AppEvent) {
            AppEvent appEvent = (AppEvent) event;
            if (!appEvent.getAppId().equals(stateManager.getAppId()) || !appEvent.getDeviceId().equals(stateManager.getDeviceId())) {
                stateManager.markAsBusy();
                return true;
            }
            return false;
        } else {
            return true;
        }
    }
}
