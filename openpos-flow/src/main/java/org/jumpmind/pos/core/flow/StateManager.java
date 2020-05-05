    /**
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

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.core.clientconfiguration.ClientConfigChangedMessage;
import org.jumpmind.pos.core.clientconfiguration.IClientConfigSelector;
import org.jumpmind.pos.core.clientconfiguration.LocaleChangedMessage;
import org.jumpmind.pos.core.clientconfiguration.LocaleMessageFactory;
import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.core.flow.config.*;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.core.service.UIDataMessageProviderService;
import org.jumpmind.pos.core.ui.Toast;
import org.jumpmind.pos.core.ui.DialogProperties;
import org.jumpmind.pos.core.service.IScreenService;
import org.jumpmind.pos.core.service.spring.DeviceScope;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.UIDataMessageProvider;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.Versions;
import org.jumpmind.pos.util.event.Event;
import org.jumpmind.pos.util.model.Message;
import org.jumpmind.pos.util.startup.DeviceStartupTaskConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component()
@org.springframework.context.annotation.Scope("prototype")
public class StateManager implements IStateManager {

    final Logger log = LoggerFactory.getLogger(getClass());
    final Logger loggerGraphical = LoggerFactory.getLogger(getClass().getName() + ".graphical");
    private final StateManagerLogger stateManagerLogger = new StateManagerLogger(loggerGraphical);

    @Autowired(required = false)
    private String deviceId;

    @Autowired
    private IScreenService screenService;

    @Autowired
    private ActionHandlerImpl actionHandler;

    @Autowired
    private Injector injector;

    @Autowired
    private Outjector outjector;

    @Autowired(required = false)
    private List<? extends ISessionListener> sessionListeners;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private UIDataMessageProviderService uiDataMessageProviderService;

    @Autowired
    private IClientConfigSelector clientConfigSelector;

    @Autowired
    private StateLifecycle stateLifecycle;

    @Autowired
    LocaleMessageFactory localeMessageFactory;

    @Autowired
    private ActionHandlerHelper helper;

    @Autowired
    DeviceStartupTaskConfig deviceStartupTaskConfig;

    private ApplicationState applicationState = new ApplicationState();

    private List<TransitionStepConfig> transitionStepConfigs;

    private FlowConfig initialFlowConfig;

    private long sessionTimeoutMillis = 0;

    private Action sessionTimeoutAction;

    private Map<String, Boolean> sessionAuthenticated = new HashMap<>();

    private Map<String, Boolean> sessionCompatible = new HashMap<>();

    private Map<String, String> clientContext = new HashMap<>();

    private IErrorHandler errorHandler;

    private EventBroadcaster eventBroadcaster;

    private AtomicReference<Date> lastInteractionTime = new AtomicReference<Date>(new Date());
    private AtomicInteger activeCalls = new AtomicInteger(0);
    private AtomicBoolean transitionRestFlag = new AtomicBoolean(false);
    private AtomicLong lastActionTimeInMs = new AtomicLong(0);
    private AtomicLong lastShowTimeInMs = new AtomicLong(0);
    private AtomicReference<Thread> activeThread = new AtomicReference<>();

    @Override
    public void reset() {
        init(getAppId(), getDeviceId());
        this.messageService.sendMessage(getAppId(), getDeviceId(), new Message(MessageType.Connected));
    }

    public void init(String appId, String nodeId) {
        this.applicationState.reset();
        this.applicationState.setAppId(appId);
        this.applicationState.setDeviceId(nodeId);
        this.eventBroadcaster = new EventBroadcaster(this);

        applicationState.getScope().setDeviceScope("stateManager", this);
        initDefaultScopeObjects();

        deviceStartupTaskConfig.processDeviceStartupTasks(nodeId, appId);

        if (initialFlowConfig != null) {
            applicationState.setCurrentContext(new StateContext(initialFlowConfig, null, null));
            sendConfigurationChangedMessage();
            // TODO: think about making this ASYNC so it doesn't hold up the rest of initialization
            transitionTo(new Action(StateManagerActionConstants.STARTUP_ACTION), initialFlowConfig.getInitialState());
        } else {
            throw new RuntimeException("Could not find a flow config for " + appId);
        }

    }

    protected void setTransitionSteps(List<TransitionStepConfig> transitionStepConfigs) {
        this.transitionStepConfigs = transitionStepConfigs;
    }

    private void initDefaultScopeObjects() {
        applicationState.getScope().setDeviceScope("additionalTagsForConfiguration", new ArrayList<String>());
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
        this.log.info("Session {} removed from cache of authenticated sessions", sessionId);
    }

    @Override
    public void setClientContext(Map<String, String> clientContext) {
        this.clientContext = clientContext;
    }

    @Override
    public Map<String, String> getClientContext(){
        return this.clientContext;
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

    protected void transitionTo(Action action, Object newState, SubFlowConfig enterSubStateConfig, StateContext resumeSuspendedState) {
        transitionTo(action, newState, enterSubStateConfig, resumeSuspendedState, false);
    }

    protected void transitionTo(Action action, Object newState, SubFlowConfig enterSubStateConfig, StateContext resumeSuspendedState,
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

        boolean enterSubState = enterSubStateConfig != null;
        stateLifecycle.executeDepart(applicationState.getCurrentContext().getState(), newState, enterSubState, action);

        TransitionResult transitionResult = executeTransition(applicationState.getCurrentContext(), newState, action);
        if (transitionResult.getTransitionResultCode() == TransitionResult.TransitionResultCode.PROCEED) {
            markAsBusy();

            boolean exitSubState = resumeSuspendedState != null;
            String returnActionName = null;
            if (exitSubState) {
                returnActionName = getReturnActionName(action);
            }

            stateManagerLogger.logStateTransition(applicationState.getCurrentContext().getState(), newState, action, returnActionName,
                    enterSubStateConfig, exitSubState ? applicationState.getCurrentContext() : null, getApplicationState(),
                    resumeSuspendedState);

            if (enterSubState) {
                applicationState.getStateStack().push(applicationState.getCurrentContext());
                applicationState.setCurrentContext(buildSubStateContext(enterSubStateConfig, action));
            } else if (exitSubState) {
                applicationState.setCurrentContext(resumeSuspendedState);
            }

            applicationState.getCurrentContext().setState(newState);

            if( transitionResult != null && transitionResult.getTransition() != null) {
                for (ITransitionStep transitionStep : transitionResult.getTransition().getTransitionSteps()) {
                    performInjections(transitionStep);
                    transitionStep.afterTransition(new TransitionContext(action, applicationState.getCurrentContext()));
                    performOutjections(transitionStep);
                }
            }

            performInjections(newState);

            if (resumeSuspendedState == null || returnActionName == null || autoTransition) {
                stateLifecycle.executeArrive(this, applicationState.getCurrentContext().getState(), action);
            } else {
                Action returnAction = new Action(returnActionName, action.getData());
                returnAction.setCausedBy(action);
                doAction(returnAction); // indirect recursion
            }
        } else {
            //TODO: discuss whether this is how we want to handle cancelled transitions
            Action cancelAction = new Action(StateManagerActionConstants.TRANSITION_CANCELLED_ACTION);
            cancelAction.setCausedBy(action);
            if (applicationState.getCurrentContext().getState() == null) {
                throw new FlowException("A transition was cancelled but there is no state to go back to. This could be a case where the first thing shown is a transition to the initial state, " +
                        "like a user name prompt leading into self-checkout for example. To correct this, adjust your flow config so there is truly an initial state to go back to when the transition is cancelled.");
            }
            // resassert the previous state before the cancelled transition
            // Note: this can be a problem if the state doesn't show a screen and just sends an action because
            // the StateManager will be busy.
            stateLifecycle.executeArrive(this, applicationState.getCurrentContext().getState(), cancelAction);
            if (transitionResult.getTransition().getQueuedAction() != null) { // allowed cancelled transitions to queue an action.
                doAction(transitionResult.getTransition().getQueuedAction());
            }
        }
    }

    protected String getReturnActionName(Action action) {
        if (applicationState.getCurrentContext().getSubFlowConfig() != null
                && applicationState.getCurrentContext().getSubFlowConfig().getReturnActionNames() != null) {

            for (String returnActionName : applicationState.getCurrentContext().getSubFlowConfig().getReturnActionNames()) {
                if (StringUtils.equals(returnActionName, action.getName())) {
                    return action.getName(); // the action IS a return action,
                    // so don't modify.
                }
            }

            if (applicationState.getCurrentContext().getSubFlowConfig().getReturnActionNames().length == 1) {
                return applicationState.getCurrentContext().getSubFlowConfig().getReturnActionNames()[0];
            } else if (applicationState.getCurrentContext().getSubFlowConfig().getReturnActionNames().length > 1) {
                throw new FlowException("Unexpected situation. Non-return action raised which completed a subflow: " + action.getName()
                        + " -- but there is more than 1 return action mapped to this subflow so we don't know which return action to pick: "
                        + Arrays.toString(applicationState.getCurrentContext().getSubFlowConfig().getReturnActionNames()));
            }
        }

        return null;
    }

    protected StateContext buildSubStateContext(SubFlowConfig enterSubStateConfig, Action action) {
        StateContext stateContext = new StateContext(enterSubStateConfig.getSubFlowConfig(), action);
        stateContext.getFlowScope().putAll(applicationState.getCurrentContext().getFlowScope());
        stateContext.setSubFlowConfig(enterSubStateConfig);
        return stateContext;
    }

    public void performInjectionsOnSpringBean(Object springBean) {
        injector.performInjectionsOnSpringBean(springBean, applicationState.getScope(), applicationState.getCurrentContext());
    }

    public void performInjections(Object stateOrStep) {
        this.log.trace("Performing injections on {}...", stateOrStep.getClass().getName());
        injector.performInjections(stateOrStep, applicationState.getScope(), applicationState.getCurrentContext());
        refreshDeviceScope();
        this.log.trace("Injections completed on {}.", stateOrStep.getClass().getName());
    }

    protected void refreshDeviceScope() {
        for (String name : new HashSet<>(applicationState.getScope().getDeviceScope().keySet())) {
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
        if (CollectionUtils.isEmpty(transitionStepConfigs)) {
            TransitionResult result = new TransitionResult();
            result.setTransitionResultCode(TransitionResult.TransitionResultCode.PROCEED);
            return result;
        }

        Transition transition = new Transition(transitionStepConfigs, sourceStateContext, newState);
        applicationState.setCurrentTransition(transition);

        /*
         * This will block.
         */
        TransitionResult.TransitionResultCode resultCode = applicationState.getCurrentTransition().execute(this, action);
        TransitionResult result = new TransitionResult();
        result.setTransitionResultCode(resultCode);
        result.setTransition(transition);
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
        Map<String, UIDataMessageProvider<?>> dataProviders = applicationState.getDataMessageProviderMap();
        uiDataMessageProviderService.resetProviders(applicationState);

        /*
         * Hang onto the dialog since showing the last screen first will clear
         * the last dialog from the screen service
         */
        UIMessage lastDialog = screenService.getLastPreInterceptedDialog(applicationState.getAppId(), applicationState.getDeviceId());
        UIMessage lastScreen = screenService.getLastPreInterceptedScreen(applicationState.getAppId(), applicationState.getDeviceId());
        if (lastScreen != null) {
            lastScreen.put("refreshAlways", true);
            showScreen(lastScreen, dataProviders);
        }
        if (lastDialog != null) {
            // Don't save a dialog if it is closable, otherwise it gets shown on
            // a refresh
            DialogProperties properties = (DialogProperties) lastDialog.get("dialogProperties");
            if (properties == null || !properties.isCloseable()) {
                lastDialog.put("refreshAlways", true);
                showScreen(lastDialog, dataProviders);
            }
        }

        Class<?> clazz = getCurrentState().getClass();

        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, OnRefresh.class, true, true);

        if( !methods.isEmpty()) {
            methods.forEach( m -> {
                m.setAccessible(true);
                try {
                    m.invoke(getCurrentState());
                } catch (Exception ex) {
                    if (errorHandler != null) {
                        errorHandler.handleError(this, ex);
                    } else {
                        throw new FlowException("Failed to invoke method " + m, ex);
                    }
                }
            });
        }

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

    @Override
    public boolean isAtRest() {
        return (activeCalls.get() == 0 || transitionRestFlag.get()) &&
                lastShowTimeInMs.longValue() >= lastActionTimeInMs.longValue();
    }

    @Override
    public void markAsBusy() {
        lastActionTimeInMs.set(System.currentTimeMillis());
        activeThread.set(Thread.currentThread());
    }

    private boolean notBusy(String actionName, boolean doNotBlockForResponse) {
        boolean notBusy = true;
        long currentActionTimeInMs = System.currentTimeMillis();

        synchronized (this) {
            if (isAtRest() || doNotBlockForResponse ||
                    activeThread.get() == null ||
                    activeThread.get().equals(Thread.currentThread()) &&
                    currentActionTimeInMs >= lastActionTimeInMs.longValue()) {
                Thread active = activeThread.get();
                log.info("Action received: {}, State manager is NOT busy.  Active calls: {}, Current thread: {}, Active thread: {}, Last display occurred after last action time: {}",
                        actionName,
                        activeCalls.get(), Thread.currentThread().getName(), active != null ? active.getName() : null, lastShowTimeInMs.get()-lastActionTimeInMs.get());
                lastInteractionTime.set(new Date());
                activeCalls.incrementAndGet();
                markAsBusy();
            } else {
                log.info("Action received: {}, State manager is busy.  Active calls: {}, Current thread: {}, Active thread: {}, Last display occurred after last action time: {}, Current action occured before last action time: {} ",
                        actionName, activeCalls.get(), Thread.currentThread().getName(), activeThread.get().getName(), lastShowTimeInMs.get()-lastActionTimeInMs.get(), lastActionTimeInMs.get()-currentActionTimeInMs);
                notBusy = false;
            }
        }
        
        return notBusy;
    }

    @Override
    public void doAction(Action action) {
        if (notBusy(action.getName(), action.isDoNotBlockForResponse())) {
            try {
                // Global action handler takes precedence over all actions (for now)
                Class<? extends Object> globalActionHandler = getGlobalActionHandler(action);
                if (globalActionHandler != null && !isCalledFromGlobalActionHandler(globalActionHandler, action)) {
                    callGlobalActionHandler(action, globalActionHandler);
                    refreshDeviceScope();
                    return;
                }

                if (applicationState.getCurrentTransition() != null) {
                    boolean handled = applicationState.getCurrentTransition().handleAction(action);
                    if (!handled) {
                        throw new FlowException("Transition step " + applicationState.getCurrentTransition().getCurrentTransitionStep() + " cannot handle action '" + action + "'");
                    }
                    return;
                }

                FlowConfig flowConfig = applicationState.getCurrentContext().getFlowConfig();
                StateConfig stateConfig = applicationState.findStateConfig(flowConfig);

                validateStateConfig(applicationState.getCurrentContext().getState(), stateConfig);

                Class<? extends Object> transitionStateClass = stateConfig.getActionToStateMapping().get(action.getName());
                Class<? extends Object> globalTransitionStateClass = flowConfig.getActionToStateMapping().get(action.getName());
                SubFlowConfig subStateConfig = stateConfig.getActionToSubStateMapping().get(action.getName());
                SubFlowConfig globalSubStateConfig = flowConfig.getActionToSubStateMapping().get(action.getName());

                // Execute state specific action handlers
                if (actionHandler.canHandleAction(applicationState.getCurrentContext().getState(), action)) {
                    handleAction(action);
                } else if (handleTerminatingState(action, stateConfig)) {
                    return;
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
                            "Unexpected action \"%s\". Either no @ActionHandler %s.on%s() method found, or no withTransition(\"%s\"...) defined in the \"%s\" flow config.",
                            action.getName(), applicationState.getCurrentContext().getState().getClass().getName(), action.getName(),
                            action.getName(), applicationState.getCurrentContext().getFlowConfig().getName()));
                }
            } catch (Throwable ex) {
                handleOrRaiseException(ex);
            } finally {
                activeCalls.decrementAndGet();
                if (action.isDoNotBlockForResponse()) {
                    lastShowTimeInMs.set(System.currentTimeMillis());
                }
            }
        } else {
            log.warn("Discarding unexpected action " + action.getName());
        }
    }

    protected void handleOrRaiseException(Throwable ex) {
        if (this.getErrorHandler() != null) {
            this.getErrorHandler().handleError(this, ex);
        } else {
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new FlowException(ex);
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
        log.info("Calling global action handler: {}", globalActionHandler.getName());

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
        List<Method> globalMethods = MethodUtils.getMethodsListWithAnnotation(globalActionHandler, OnGlobalAction.class)
                .stream()
                .filter( method -> method.getName().equals("on" + action.getName()))
                .collect(Collectors.toList());
        for (Method method : globalMethods) {
            invokeGlobalAction(action, method, actionHandler);
        }
    }

    protected boolean isCalledFromGlobalActionHandler(Object handler, Action action) {
        StackTraceElement[] currentStack = Thread.currentThread().getStackTrace();

        if (currentStack.length > 150) {
            helper.checkStackOverflow(StateManager.class, handler, currentStack);
        }

        for (StackTraceElement stackFrame : currentStack) {
            Class<?> currentClass = helper.getClassFrom(stackFrame);
            if (currentClass != null && !Modifier.isAbstract(currentClass.getModifiers()) && FlowUtil.isGlobalActionHandler(currentClass)
                    && !currentClass.getName().equals(((Class)handler).getName())) {
                return false;
            } else if (stackFrame.getClassName().equals(((Class)handler).getName())) {
                return true;
            }

        }

        return false;
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
                    SubFlowConfig autoSubFlowConfig = suspendedStateConfig.getActionToSubStateMapping().get(returnAction);
                    if (autoSubFlowConfig != null) {
                        applicationState.setCurrentContext(suspendedState);
                        transitionToSubState(action, autoSubFlowConfig);
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

    protected void transitionToSubState(Action action, SubFlowConfig subStateConfig) {
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
        for (String name : new HashSet<>(applicationState.getScope().getDeviceScope().keySet())) {
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

        lastShowTimeInMs.set(System.currentTimeMillis());
    }


    @Override
    public void showScreen(UIMessage screen, Map<String, UIDataMessageProvider<?>> dataMessageProviderMap) {
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

        screenService.showScreen(applicationState.getAppId(), applicationState.getDeviceId(), screen, dataMessageProviderMap);

        lastShowTimeInMs.set(System.currentTimeMillis());
    }

    @SuppressWarnings("unchecked")
    @Override
    public void showScreen(UIMessage screen) {
        showScreen(screen, null);
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
            Action localSessionTimeoutAction = sessionTimeoutAction != null ? sessionTimeoutAction : Action.ACTION_TIMEOUT;
            localSessionTimeoutAction.setDoNotBlockForResponse(true);
            doAction(localSessionTimeoutAction);
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
            log.info("Registering query params " + queryParams.toString());
            applicationState.getScope().setScopeValue(ScopeType.Device, "queryParams", queryParams);
        }
    }

    @Override
    public void registerPersonalizationProperties(Map<String, String> personalizationProperties) {
        log.info("Registering personalization properties " + personalizationProperties.toString());
        applicationState.getScope().setScopeValue(ScopeType.Device, "personalizationProperties", personalizationProperties);
    }

    public Injector getInjector() {
        return injector;
    }

    public void setTransitionRestFlag(boolean transitionRestFlag) {
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
            log.info("An {} is not configured. Will not be sending clientconfiguration configuration to the client",
                    IClientConfigSelector.class.getSimpleName());
        }
    }

    protected void onEvent(Event event) {
        if (notBusy(event.toString(), true)) {
            try {
                List<Class> classes = initialFlowConfig.getEventHandlers();
                classes.forEach(clazz -> eventBroadcaster.postEventToObject(clazz, event));

                Object state = getCurrentState();
                if (state != null) {
                    if (!eventBroadcaster.postEventToObject(state, event)) {
                        lastShowTimeInMs.set(System.currentTimeMillis());
                    }
                }
            } finally {
                activeCalls.decrementAndGet();
            }
        } else {
            log.warn("Discarding unexpected event " + event.toString());
        }
    }
    
    @Override
    public long getLastActionTimeInMs() {
        return lastActionTimeInMs.get();
    }
}
