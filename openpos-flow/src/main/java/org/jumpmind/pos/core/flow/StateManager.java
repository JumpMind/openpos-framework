/**
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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.core.clientconfiguration.ClientConfigChangedMessage;
import org.jumpmind.pos.core.clientconfiguration.IClientConfigSelector;
import org.jumpmind.pos.core.clientconfiguration.LocaleChangedMessage;
import org.jumpmind.pos.core.clientconfiguration.LocaleMessageFactory;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.StateConfig;
import org.jumpmind.pos.core.flow.config.SubTransition;
import org.jumpmind.pos.core.screen.Toast;
import org.jumpmind.pos.core.service.IScreenService;
import org.jumpmind.pos.core.service.spring.DeviceScope;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.Versions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component()
@org.springframework.context.annotation.Scope("prototype")
public class StateManager implements IStateManager {

    final Logger logger = LoggerFactory.getLogger(getClass());
    final Logger loggerGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");
    private final StateManagerLogger stateManagerLogger = new StateManagerLogger(loggerGraphical);

    @Autowired
    private IScreenService screenService;

    @Autowired
    private ActionHandlerImpl actionHandler;

    @Autowired
    private Injector injector;

    @Autowired
    private Outjector outjector;

    @Autowired
    private ApplicationStateSerializer applicationStateSerializer;

    @Autowired(required = false)
    private List<? extends ITransitionStep> transitionSteps;

    @Autowired(required = false)
    private List<? extends ISessionTimeoutListener> sessionTimeoutListeners;

    @Autowired(required = false)
    private List<? extends ISessionListener> sessionListeners;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private IClientConfigSelector clientConfigSelector;

    private ApplicationState applicationState = new ApplicationState();

    @Value("${org.jumpmind.pos.core.flow.StateManager.autoSaveState:false}")
    private boolean autoSaveState = false;

    @Autowired
    private StateLifecycle stateLifecyce;

    @Autowired
    LocaleMessageFactory localeMessageFactory;

    private FlowConfig initialFlowConfig;

    private AtomicReference<Date> lastInteractionTime = new AtomicReference<Date>(new Date());

    private long sessionTimeoutMillis = 0;

    private Action sessionTimeoutAction;

    private Map<String, Boolean> sessionAuthenticated = new HashMap<>();

    private Map<String, Boolean> sessionCompatible = new HashMap<>();

    private IErrorHandler errorHandler;

    private final AtomicInteger activeCalls = new AtomicInteger(0);
    private final AtomicBoolean transitionRestFlag = new AtomicBoolean(false);

    public void init(String appId, String nodeId) {
        this.applicationState.setAppId(appId);
        this.applicationState.setDeviceId(nodeId);

        boolean resumeState = false;

        if (autoSaveState) {
            try {
                applicationState = applicationStateSerializer.deserialize(this, "./openpos-state.json");
                resumeState = true;
            } catch (FlowException ex) {
                logger.info(ex.getMessage());
            } catch (Exception ex) {
                logger.warn("Failed to load openpos-state.json", ex);
            }
        }

        applicationState.getScope().setDeviceScope("stateManager", this);



        if (resumeState) {
            sendConfigurationChangedMessage();
            refreshScreen();
        } else if (initialFlowConfig != null) {
            applicationState.setCurrentContext(new StateContext(initialFlowConfig, null, null));
            sendConfigurationChangedMessage();
            // TODO: think about making this ASYNC so it doesn't hold up the rest of initialization
            transitionTo(new Action("Startup"), initialFlowConfig.getInitialState());
        } else {
            throw new RuntimeException("Could not find a flow config for " + appId);
        }

    }

    public void setErrorHandler(IErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public IErrorHandler getErrorHandler() {
        return errorHandler;
    }

    @Override
    public void setSessionAuthenticated(String sessionId, boolean authenticated) {
        this.sessionAuthenticated.put(sessionId, authenticated);
        if (this.sessionListeners != null && authenticated) {
            for (ISessionListener sessionListener : sessionListeners) {
                sessionListener.connected(sessionId, this);
            }
        }
    }

    public void removeSessionAuthentication(String sessionId) {
        if (this.sessionListeners != null && sessionAuthenticated.containsKey(sessionId)) {
            for (ISessionListener sessionListener : sessionListeners) {
                sessionListener.disconnected(sessionId, this);
            }
        }
        this.sessionAuthenticated.remove(sessionId);
        this.logger.info("Session {} removed from cache of authenticated sessions", sessionId);
    }

    @Override
    public boolean isSessionAuthenticated(String sessionId) {
        return this.sessionAuthenticated.get(sessionId) != null && this.sessionAuthenticated.get(sessionId);
    }

    @Override
    public boolean areAllSessionsAuthenticated() {
        return !sessionAuthenticated.values().contains(false);
    }

    @Override
    public void setSessionCompatible(String sessionId, boolean compatible) {
        this.sessionCompatible.put(sessionId, compatible);
    }

    @Override
    public boolean isSessionCompatible(String sessionId) {
        return this.sessionCompatible.get(sessionId) != null && this.sessionCompatible.get(sessionId);
    }

    @Override
    public boolean areAllSessionsCompatible() {
        return !sessionCompatible.values().contains(false);
    }

    public void removeSessionCompatible(String sessionId) {
        this.sessionCompatible.remove(sessionId);
    }

    protected void transitionTo(Action action, StateConfig stateConfig) {
        Object newState = buildState(stateConfig);
        transitionTo(action, newState);
    }

    @Override
    public void transitionTo(Action action, Object newState) {
        transitionTo(action, newState, null, null);
    }

    protected void transitionTo(Action action, Object newState, SubTransition enterSubStateConfig, StateContext resumeSuspendedState) {
        transitionTo(action, newState, enterSubStateConfig, resumeSuspendedState, false);
    }

    protected void transitionTo(Action action, Object newState, SubTransition enterSubStateConfig, StateContext resumeSuspendedState,
            boolean autoTransition) {
        if (applicationState.getCurrentContext() == null) {
            throw new FlowException(
                    "There is no applicationState.getCurrentContext() on this StateManager.  HINT: States should use @In to get the StateManager, not @Autowired.");
        }

        if (enterSubStateConfig != null && resumeSuspendedState != null) {
            throw new FlowException(
                    "enterSubStateConfig and resumeSuspendedState should not BOTH be provided at the same time. enterSubStateConfig implies entering a subState, "
                            + "while resumeSuspendedState implies "
                            + "existing a subState. These two should be be happening at the same time.");
        }

        if (applicationState.getCurrentContext().getState() != null) {
            performOutjections(applicationState.getCurrentContext().getState());
        }

        TransitionResult transitionResult = executeTransition(applicationState.getCurrentContext(), newState, action);
        if (transitionResult == TransitionResult.PROCEED) {
            boolean enterSubState = enterSubStateConfig != null;
            boolean exitSubState = resumeSuspendedState != null;
            String returnActionName = null;
            if (exitSubState) {
                returnActionName = getReturnActionName(action);
            }

            stateManagerLogger.logStateTransition(applicationState.getCurrentContext().getState(), newState, action, returnActionName,
                    enterSubStateConfig, exitSubState ? applicationState.getCurrentContext() : null, getApplicationState(),
                    resumeSuspendedState);

            stateLifecyce.executeDepart(applicationState.getCurrentContext().getState(), newState, enterSubState, action);

            if (enterSubState) {
                applicationState.getStateStack().push(applicationState.getCurrentContext());
                applicationState.setCurrentContext(buildSubStateContext(enterSubStateConfig, action));
            } else if (exitSubState) {
                applicationState.setCurrentContext(resumeSuspendedState);
            }

            applicationState.getCurrentContext().setState(newState);

            performInjections(newState);

            if (resumeSuspendedState == null || returnActionName == null || autoTransition) {
                stateLifecyce.executeArrive(this, applicationState.getCurrentContext().getState(), action);
            } else {
                Action returnAction = new Action(returnActionName, action.getData());
                returnAction.setCausedBy(action);
                doAction(returnAction); // indirect recursion
            }
        } else {
            stateLifecyce.executeArrive(this, applicationState.getCurrentContext().getState(), action);
        }

    }

    protected String getReturnActionName(Action action) {
        if (applicationState.getCurrentContext().getSubTransition() != null
                && applicationState.getCurrentContext().getSubTransition().getReturnActionNames() != null) {

            for (String returnActionName : applicationState.getCurrentContext().getSubTransition().getReturnActionNames()) {
                if (StringUtils.equals(returnActionName, action.getName())) {
                    return action.getName(); // the action IS a return action,
                    // so don't modify.
                }
            }

            if (applicationState.getCurrentContext().getSubTransition().getReturnActionNames().length == 1) {
                return applicationState.getCurrentContext().getSubTransition().getReturnActionNames()[0];
            } else if (applicationState.getCurrentContext().getSubTransition().getReturnActionNames().length > 1) {
                throw new FlowException("Unpected situation. Non-return action raised which completed a subflow: " + action.getName()
                        + " -- but there is more than 1 return action mapped to this subflow so we don't know which return action to pick: "
                        + Arrays.toString(applicationState.getCurrentContext().getSubTransition().getReturnActionNames()));
            }
        }

        return null;
    }

    protected StateContext buildSubStateContext(SubTransition enterSubStateConfig, Action action) {
        StateContext stateContext = new StateContext(enterSubStateConfig.getSubFlowConfig(), action);
        stateContext.getFlowScope().putAll(applicationState.getCurrentContext().getFlowScope());
        stateContext.setSubTransition(enterSubStateConfig);
        return stateContext;
    }

    public void performInjectionsOnSpringBean(Object springBean) {
        injector.performInjectionsOnSpringBean(springBean, applicationState.getScope(), applicationState.getCurrentContext());
    }

    public void performInjections(Object stateOrStep) {
        injector.performInjections(stateOrStep, applicationState.getScope(), applicationState.getCurrentContext());
        refreshDeviceScope();
    }

    protected void refreshDeviceScope() {
        for (String name : applicationState.getScope().getDeviceScope().keySet()) {
            Object value = applicationState.getScopeValue(ScopeType.Device, name);
            performOutjections(value);
            if (DeviceScope.isDeviceScope(name)) {
                performInjectionsOnSpringBean(value);
            } else {
                injector.performInjections(value, applicationState.getScope(), applicationState.getCurrentContext());
            }
        }
    }

    public void performOutjections(Object stateOrStep) {
        outjector.performOutjections(stateOrStep, applicationState.getScope(), applicationState.getCurrentContext());
    }

    protected TransitionResult executeTransition(StateContext sourceStateContext, Object newState, Action action) {
        if (CollectionUtils.isEmpty(transitionSteps)) {
            return TransitionResult.PROCEED;
        }

        applicationState.setCurrentTransition(new Transition(transitionSteps, sourceStateContext, newState));

        /*
         * This will block.
         */
        TransitionResult result = applicationState.getCurrentTransition().execute(this, action);
        applicationState.setCurrentTransition(null);
        return result;
    }

    protected Object buildState(StateConfig stateConfig) {
        return createNewState(stateConfig.getStateClass());
    }

    @Override
    public Object getCurrentState() {
        return applicationState.getCurrentContext().getState();
    }

    @Override
    public void refreshScreen() {
        /*
         * Hang onto the dialog since showing the last screen first will clear
         * the last dialog from the screen service
         */
        UIMessage lastDialog = screenService.getLastPreInterceptedDialog(applicationState.getAppId(), applicationState.getDeviceId());
        UIMessage lastScreen = screenService.getLastPreInterceptedScreen(applicationState.getAppId(), applicationState.getDeviceId());
        if (lastScreen != null) {
            lastScreen.put("refreshAlways", true);
        }
        if (lastDialog != null) {
            lastDialog.put("refreshAlways", true);
        }
        showScreen(lastScreen);
        showScreen(lastDialog);
    }

    // Could come from a UI or a running state..
    @Override
    public void doAction(String actionName) {
        doAction(actionName, null);
    }

    @Override
    public void doAction(String actionName, Map<String, String> params) {
        Action action = new Action(actionName, params);
        doAction(action);
    }

    @Override
    public void keepAlive() {
        lastInteractionTime.set(new Date());
    }

    public boolean isAtRest() {
        return activeCalls.get() == 0 || transitionRestFlag.get();
    }

    @Override
    public void doAction(Action action) {
        lastInteractionTime.set(new Date());
        activeCalls.incrementAndGet();

        try {
            // Global action handler takes precedence over all actions (for now)
            Class<? extends Object> globalActionHandler = getGlobalActionHandler(action);
            if (globalActionHandler != null) {
                callGlobalActionHandler(action, globalActionHandler);
                return;
            }

            if (applicationState.getCurrentTransition() != null) {
                applicationState.getCurrentTransition().handleAction(action);
                return;
            }

            FlowConfig flowConfig = applicationState.getCurrentContext().getFlowConfig();
            StateConfig stateConfig = applicationState.findStateConfig(flowConfig);

            if (handleTerminatingState(action, stateConfig)) {
                return;
            }

            validateStateConfig(applicationState.getCurrentContext().getState(), stateConfig);

            Class<? extends Object> transitionStateClass = stateConfig.getActionToStateMapping().get(action.getName());
            Class<? extends Object> globalTransitionStateClass = flowConfig.getActionToStateMapping().get(action.getName());
            SubTransition subStateConfig = stateConfig.getActionToSubStateMapping().get(action.getName());
            SubTransition globalSubStateConfig = flowConfig.getActionToSubStateMapping().get(action.getName());

            // Execute state specific action handlers
            if (actionHandler.canHandleAction(applicationState.getCurrentContext().getState(), action)) {
                handleAction(action);
            } else if (transitionStateClass != null) {
                // Execute state transition
                transitionToState(action, transitionStateClass);
            } else if (subStateConfig != null) {
                // Execute sub-state transition
                transitionToSubState(action, subStateConfig);
            } else if (actionHandler.canHandleAnyAction(applicationState.getCurrentContext().getState(), action)) {
                // Execute action handler for onAnyAction
                actionHandler.handleAnyAction(this, applicationState.getCurrentContext().getState(), action);
            } else if (globalTransitionStateClass != null) {
                // Execute global state transition
                handleGlobalStateTransition(action, globalTransitionStateClass);
            } else if (globalSubStateConfig != null) {
                // Execute global sub-state transition
                transitionToSubState(action, globalSubStateConfig);
            } else {
                throw new FlowException(String.format(
                        "Unexpected action \"%s\". Either no @ActionHandler %s.on%s() method found, or no withTransition(\"%s\"...) defined in the flow config.",
                        action.getName(), applicationState.getCurrentContext().getState().getClass().getName(), action.getName(),
                        action.getName()));
            }
        } finally {
            activeCalls.decrementAndGet();
        }
    }

    protected Class<? extends Object> getGlobalActionHandler(Action action) {
        FlowConfig flowConfig = applicationState.getCurrentContext().getFlowConfig();
        Class<? extends Object> currentActionHandler = flowConfig.getActionToStateMapping().get(action.getName());
        LinkedList<StateContext> stateStack = applicationState.getStateStack();

        if (isActionHandler(currentActionHandler)) {
            return currentActionHandler;
        } else {
            for (StateContext state : stateStack) {
                Class<? extends Object> actionHandler = state.getFlowConfig().getActionToStateMapping().get(action.getName());
                if (isActionHandler(actionHandler)) {
                    return actionHandler;
                }
            }
        }

        return null;
    }

    protected boolean isState(Class<? extends Object> stateOrActionHandler) {
        if (stateOrActionHandler != null) {
            List<Method> arriveMethods = MethodUtils.getMethodsListWithAnnotation(stateOrActionHandler, OnArrive.class, true, true);
            return arriveMethods != null && !arriveMethods.isEmpty();
        }
        return false;
    }

    protected boolean isActionHandler(Class<? extends Object> stateOrActionHandler) {
        if (stateOrActionHandler != null) {
            List<Method> globalMethods = MethodUtils.getMethodsListWithAnnotation(stateOrActionHandler, OnGlobalAction.class);
            return globalMethods != null && !globalMethods.isEmpty();
        }
        return false;
    }

    private void callGlobalActionHandler(Action action, Class<? extends Object> globalActionHandler) {
        logger.info("Calling global action handler: {}", globalActionHandler.getName());

        if (isState(globalActionHandler) && isActionHandler(globalActionHandler)) {
            throw new FlowException("Class cannot implement @OnArrive and @OnGlobalAction: " + globalActionHandler.getClass().getName());
        }

        Object actionHandler;
        try {
            actionHandler = globalActionHandler.newInstance();
        } catch (Exception ex) {
            throw new FlowException("Failed to execute global action handler: " + globalActionHandler, ex);
        }
        performInjections(actionHandler);
        List<Method> globalMethods = MethodUtils.getMethodsListWithAnnotation(globalActionHandler, OnGlobalAction.class);
        for (Method method : globalMethods) {
            invokeGlobalAction(action, method, actionHandler);
        }
    }

    protected void invokeGlobalAction(Action action, Method method, Object actionHandler) {
        try {
            if (method.getParameters() != null && method.getParameters().length == 1) {
                method.invoke(actionHandler, action);
            } else {
                method.invoke(actionHandler);
            }
        } catch (Exception ex) {
            throw new FlowException("Failed to execute global action handler. Method: " + method + " actionHandler: " + actionHandler, ex);
        }
    }

    protected void handleGlobalStateTransition(Action action, Class<? extends Object> stateOrActionHandler) {
        if (isState(stateOrActionHandler) && isActionHandler(stateOrActionHandler)) {
            throw new FlowException("Class cannot implement @OnArrive and @OnGlobalAction: " + stateOrActionHandler.getClass().getName());
        } else if (isState(stateOrActionHandler)) {
            transitionToState(action, stateOrActionHandler);
        }
    }

    public void setScopeValue(ScopeType scopeType, String name, Object value) {
        applicationState.getScope().setScopeValue(scopeType, name, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getScopeValue(ScopeType scopeType, String name) {
        return (T) applicationState.getScopeValue(scopeType, name);
    }

    @SuppressWarnings("unchecked")
    public <T> T getScopeValue(String name) {
        return (T) applicationState.getScopeValue(name);
    }

    protected boolean handleAction(Object state, Action action) {
        return actionHandler.handleAction(this, state, action);
    }

    protected boolean handleAction(Action action) {
        return handleAction(applicationState.getCurrentContext().getState(), action);
    }

    protected boolean handleTerminatingState(Action action, StateConfig stateConfig) {
        if (stateConfig == null || stateConfig.getActionToStateMapping() == null) {
            return false;
        }

        Class<? extends Object> targetStateClass = stateConfig.getActionToStateMapping().get(action.getName());

        if (targetStateClass == null) {
            targetStateClass = applicationState.getCurrentContext().getFlowConfig().getActionToStateMapping().get(action.getName());
        }

        if (CompleteState.class == targetStateClass) {
            if (!applicationState.getStateStack().isEmpty()) {
                StateContext suspendedState = applicationState.getStateStack().pop();
                StateConfig suspendedStateConfig = suspendedState.getFlowConfig().getStateConfig(suspendedState.getState());

                String returnAction = null;
                if (applicationState.getCurrentContext().isSubstateReturnAction(action.getName())) {
                    returnAction = action.getName();
                } else {
                    returnAction = applicationState.getCurrentContext().getPrimarySubstateReturnAction();
                }

                Class<? extends Object> autoTransitionStateClass = suspendedStateConfig.getActionToStateMapping().get(returnAction);
                if (autoTransitionStateClass != null && autoTransitionStateClass != CompleteState.class) {
                    transitionTo(action, createNewState(autoTransitionStateClass), null, suspendedState, true);
                } else {
                    SubTransition autoSubTransition = suspendedStateConfig.getActionToSubStateMapping().get(returnAction);
                    if (autoSubTransition != null) {
                        applicationState.setCurrentContext(suspendedState);
                        transitionToSubState(action, autoSubTransition);
                    } else {
                        transitionTo(action, suspendedState.getState(), null, suspendedState);
                    }
                }
            } else {
                throw new FlowException("No suspended state to return to for terminating action " + action + " from state "
                        + applicationState.getCurrentContext().getState());
            }
            return true;
        } else {
            return false;
        }
    }

    protected void transitionToSubState(Action action, SubTransition subStateConfig) {
        Class<? extends Object> subState = subStateConfig.getSubFlowConfig().getInitialState().getStateClass();
        transitionTo(action, createNewState(subState), subStateConfig, null);
    }

    protected Object createNewState(Class<? extends Object> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception ex) {
            throw new FlowException("Failed to create state " + clazz.getName(), ex);
        }
    }

    protected void transitionToState(Action action, Class<? extends Object> newStateClass) {
        StateConfig newStateConfig = applicationState.getCurrentContext().getFlowConfig().getStateConfig(newStateClass);
        if (newStateConfig != null) {
            transitionTo(action, newStateConfig);
        } else {
            throw new FlowException(String.format("No State found for class \"%s\"", newStateClass));
        }
    }

    protected void validateStateConfig(Object state, StateConfig stateConfig) {
        if (stateConfig == null) {
            throw new FlowException("No configuration found for state. \"" + state
                    + "\" This state needs to be mapped in a IFlowConfigProvider implementation. ");
        }
    }

    @Override
    public void timeout() {
        FlowConfig flowConfig = applicationState.getCurrentContext().getFlowConfig();
        if (!applicationState.getStateStack().isEmpty()) {
            StateContext suspendedState = applicationState.getStateStack().pop();
            transitionTo(Action.ACTION_TIMEOUT, suspendedState.getState(), null, suspendedState);
        } else {
            transitionTo(Action.ACTION_TIMEOUT, flowConfig.getInitialState());
        }

    }

    @Override
    public void endConversation() {
        applicationState.getScope().clearConversationScope();
        clearScopeOnStates(ScopeType.Conversation);
        clearScopeOnDeviceScopeBeans(ScopeType.Conversation);
        refreshDeviceScope();
    }

    private void clearScopeOnStates(ScopeType scopeType) {
        List<StateContext> stack = applicationState.getStateStack();
        for (StateContext stateContext : stack) {
            Object state = stateContext.getState();
            injector.resetInjections(state, scopeType);
        }

        injector.resetInjections(applicationState.getCurrentContext().getState(), scopeType);
    }

    private void clearScopeOnDeviceScopeBeans(ScopeType scopeType) {
        for (String name : applicationState.getScope().getDeviceScope().keySet()) {
            Object value = applicationState.getScopeValue(ScopeType.Device, name);
            injector.resetInjections(value, scopeType);
        }
    }

    @Override
    public void endSession() {
        applicationState.getScope().clearSessionScope();
        clearScopeOnStates(ScopeType.Session);
        clearScopeOnDeviceScopeBeans(ScopeType.Session);
        refreshDeviceScope();
    }

    public void setInitialFlowConfig(FlowConfig initialFlowConfig) {
        this.initialFlowConfig = initialFlowConfig;
    }

    @Override
    public void showToast(Toast toast) {
        keepAlive();

        if (applicationState.getCurrentContext() == null) {
            throw new FlowException(
                    "There is no applicationState.getCurrentContext() on this StateManager.  HINT: States should use @In(scope=ScopeType.Node) to get the StateManager, not @Autowired.");
        }

        screenService.showToast(applicationState.getAppId(), applicationState.getDeviceId(), toast);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void showScreen(UIMessage screen) {
        keepAlive();

        if (applicationState.getCurrentContext() == null) {
            throw new FlowException(
                    "There is no applicationState.getCurrentContext() on this StateManager.  HINT: States should use @In(scope=ScopeType.Node) to get the StateManager, not @Autowired.");
        }
        if (applicationState.getCurrentContext().getState() != null
                && applicationState.getCurrentContext().getState() instanceof IMessageInterceptor) {
            ((IMessageInterceptor<UIMessage>) applicationState.getCurrentContext().getState()).intercept(applicationState.getAppId(),
                    applicationState.getDeviceId(), screen);
        }

        if (screen != null) {
            sessionTimeoutMillis = screen.getSessionTimeoutMillis();
            sessionTimeoutAction = screen.getSessionTimeoutAction();
        } else {
            sessionTimeoutMillis = 0;
            sessionTimeoutAction = null;
        }

        screenService.showScreen(applicationState.getAppId(), applicationState.getDeviceId(), screen);

    }

    @Override
    public String getNodeId() {
        return applicationState.getDeviceId();
    }

    @Override
    public String getDeviceId() {
        return applicationState.getDeviceId();
    }

    @Override
    public String getAppId() {
        return applicationState.getAppId();
    }

    // called from a Timer thread.
    public void checkSessionTimeout() {
        if (sessionTimeoutMillis > 0) {
            long inactiveMillis = System.currentTimeMillis() - lastInteractionTime.get().getTime();
            if (inactiveMillis > sessionTimeoutMillis) {
                sessionTimeout();
            }
        }
    }

    protected void sessionTimeout() {
        try {
            logger.info(String.format("Node %s session timed out.", applicationState.getDeviceId()));
            if (!CollectionUtils.isEmpty(sessionTimeoutListeners)) {
                Action localSessionTimeoutAction = sessionTimeoutAction != null ? sessionTimeoutAction : new Action("Timeout");
                for (ISessionTimeoutListener sessionTimeoutListener : sessionTimeoutListeners) {
                    sessionTimeoutListener.onSessionTimeout(this, localSessionTimeoutAction);
                }
            }
        } catch (Exception ex) {
            logger.error("Failed to process the session timeout", ex);
        }
    }

    @Override
    public ApplicationState getApplicationState() {
        return applicationState;
    }

    public void setApplicationState(ApplicationState applicationState) {
        this.applicationState = applicationState;
    }

    @Override
    public void registerQueryParams(Map<String, Object> queryParams) {
        if (queryParams != null) {
            logger.info("Registering query params " + queryParams.toString());
            applicationState.getScope().setScopeValue(ScopeType.Device, "queryParams", queryParams);
        }
    }

    @Override
    public void registerPersonalizationProperties(Map<String, String> personalizationProperties) {
        logger.info("Registering personalization properties " + personalizationProperties.toString());
        applicationState.getScope().setScopeValue(ScopeType.Device, "personalizationProperties", personalizationProperties);
    }

    public Injector getInjector() {
        return injector;
    }

    public void setTransactionRestFlag(boolean transitionRestFlag) {
        this.transitionRestFlag.set(transitionRestFlag);
    }

    public void sendConfigurationChangedMessage() {
        String appId = applicationState.getAppId();
        String deviceId = applicationState.getDeviceId();

        Map<String, String> properties = applicationState.getScopeValue("personalizationProperties");
        List<String> additionalTags = applicationState.getScopeValue("additionalTagsForConfiguration");

        try {
            if (clientConfigSelector != null) {
                Map<String, Map<String, String>> configs = clientConfigSelector.getConfigurations(properties, additionalTags);
                configs.forEach((name, clientConfiguration) -> messageService.sendMessage(appId, deviceId,
                        new ClientConfigChangedMessage(name, clientConfiguration)));
            }

            // Send versions
            ClientConfigChangedMessage versionConfiguration = new ClientConfigChangedMessage("versions");
            versionConfiguration.put("versions", Versions.getVersions());
            messageService.sendMessage(appId, deviceId, versionConfiguration);

            // Send supported locales
            LocaleChangedMessage localeMessage = localeMessageFactory.getMessage();
            messageService.sendMessage(appId, deviceId, localeMessage);

        } catch (NoSuchBeanDefinitionException e) {
            logger.info("An {} is not configured. Will not be sending clientconfiguration configuration to the client",
                    IClientConfigSelector.class.getSimpleName());
        }
    }
}
