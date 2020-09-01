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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.core.flow.config.IFlowConfigProvider;
import org.jumpmind.pos.core.flow.config.TransitionStepConfig;
import org.jumpmind.pos.core.service.IScreenService;
import org.jumpmind.pos.util.AppUtils;
import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.Event;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import static org.jumpmind.pos.util.AppUtils.setupLogging;

@Component
@Slf4j
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
            stateManager = applicationContext.getBean(StateManager.class);
            setCurrentStateManager(stateManager);
            clientContext.put("deviceId", deviceId);
            clientContext.put("appId", appId);
            personalizationProperties.entrySet().forEach(entry -> clientContext.put(entry.getKey(), entry.getValue()) );

            stateManager.setTransitionSteps(createTransitionSteps(appId, deviceId));
            stateManager.registerQueryParams(queryParams);
            stateManager.registerPersonalizationProperties(personalizationProperties);
            stateManager.setErrorHandler(errorHandler);
            stateManager.setInitialFlowConfig(flowConfigProvider.getConfig(appId, deviceId));
            stateManagersByNodeId.put(deviceId, stateManager);
            stateManager.init(appId, deviceId);
        }
        return stateManager;
    }

    private List<TransitionStepConfig> createTransitionSteps(String appId, String deviceId) {
        List<TransitionStepConfig> transitionStepConfigs = flowConfigProvider.getTransitionStepConfig(appId, deviceId);
        if (CollectionUtils.isEmpty(transitionStepConfigs)) {
            log.info("No configured transition steps found for appId {} deviceId {}. Using discovered steps from Spring.", appId, deviceId);
            transitionStepConfigs = createTransitionStepsFromSpring(appId, deviceId);
        }
        return transitionStepConfigs;
    }

    private List<TransitionStepConfig> createTransitionStepsFromSpring(String appId, String deviceId) {
        List<TransitionStepConfig> steps = new ArrayList<>();
        String[] names = applicationContext.getBeanNamesForType(ITransitionStep.class);
        for (String name : names) {
            TransitionStepConfig config = new TransitionStepConfig();
            config.setTransitionStepClass((Class<? extends ITransitionStep>) applicationContext.getBean(name).getClass());
            steps.add(config);
        }

        Collections.sort(steps, (o1, o2) -> {
            Integer o1order = 0;
            Integer o2order = 0;
            try {
                o1order = o1.getTransitionStepClass().getAnnotation(Order.class).value();
            } catch (NullPointerException ex) {
            }
            try {
                o2order = o2.getTransitionStepClass().getAnnotation(Order.class).value();
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
            IStateManager stateManager = stateManagersByNodeId.remove(deviceId);
            if (stateManager != null) {
                stateManager.stop();
            }
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
            setupLogging(stateManager.getAppId(), stateManager.getDeviceId());
            for (String property : stateManager.getClientContext().keySet()) {
                clientContext.put(property, stateManager.getClientContext().get(property));
            }

            clientContext.put("deviceId", stateManager.getDeviceId());
            clientContext.put("appId", stateManager.getAppId());
        } else {
            setupLogging("server");

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
                    setCurrentStateManager(stateManager);
                    stateManager.onEvent(event);
                } finally {
                    setCurrentStateManager(null);
                }
            }
        }
    }

}
