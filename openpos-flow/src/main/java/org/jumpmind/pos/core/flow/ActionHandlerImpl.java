package org.jumpmind.pos.core.flow;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.core.flow.config.FlowUtil;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// TODO should be called just ActionHandler, need to repackage annotation of the same name.
@Component
@org.springframework.context.annotation.Scope("prototype")
public class ActionHandlerImpl {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String METHOD_ON_ANY = "onAnyAction";

    @Autowired
    private IBeforeActionService beforeActionService;

    @Autowired(required = false)
    private IErrorHandler errorHandler;

    public boolean canHandleAction(Object state, Action action) {
        // if there is an action handler
        // AND it's not the current state firing this action.
        Method actionMethod = getActionMethod(state, action);
        if (actionMethod != null && !isCalledFromState(state, action)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean handleAction(IStateManager stateManager, Object state, Action action) {
        // Get list of @BeforeAction methods in the state and execute them first
        beforeActionService.executeBeforeActionMethods(stateManager, state, action);

        Method actionMethod = getActionMethod(state, action);
        if (actionMethod != null) {
            invokeActionMethod(stateManager, state, action, actionMethod);
            return true;
        } else {
            return false;
        }
    }

    public boolean canHandleAnyAction(Object state, Action action) {
        return getAnyActionMethod(state) != null && !isCalledFromState(state, action);
    }

    public boolean handleAnyAction(IStateManager stateManager, Object state, Action action) {
        // Get list of @BeforeAction methods in the state and execute them first
        beforeActionService.executeBeforeActionMethods(stateManager, state, action);
        
        Method anyActionMethod = getAnyActionMethod(state);
        if (anyActionMethod != null) {
            invokeActionMethod(stateManager, state, action, anyActionMethod);
            return true;
        } else {
            return false;
        }
    }

    protected Method getAnyActionMethod(Object state) {
        Class<?> clazz = state.getClass();

        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, ActionHandler.class, true, true);

        for (Method method : methods) {
            if (METHOD_ON_ANY.equals(method.getName())) {
                return method;
            }
        }

        return null;
    }

    protected Method getActionMethod(Object state, Action action) {
        Class<?> clazz = state.getClass();

        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, ActionHandler.class, true, true);

        String actionName = action.getName();

        for (Method method : methods) {
            String matchingMethodName = "on" + actionName;
            method.setAccessible(true);
            if (matchingMethodName.equals(method.getName())) {
                return method;
            }
        }

        return null;
    }

    protected void invokeActionMethod(IStateManager stateManager, Object state, Action action, Method method) {
        method.setAccessible(true);
        List<Object> arguments = new ArrayList<Object>();
        try {
            for (Class<?> type : method.getParameterTypes()) {
                if (type.isAssignableFrom(Action.class)) {
                    arguments.add(action);
                } else if (action.getData() != null && type.isAssignableFrom(action.getData().getClass())) {
                    arguments.add(action.getData());
                } else {
                    arguments.add(null);
                }
            }

            method.invoke(state, arguments.toArray(new Object[arguments.size()]));
        } catch (Exception ex) {
            if (errorHandler != null) {
                errorHandler.handleError(stateManager, ex);
            } else {
                throw new FlowException("Failed to invoke method " + method, ex);
            }
        }
    }

    protected boolean isCalledFromState(Object state, Action action) {
        StackTraceElement[] currentStack = Thread.currentThread().getStackTrace();

        if (currentStack.length > 150) {
            checkStackOverflow(currentStack);
        }

        for (StackTraceElement stackFrame : currentStack) {
            Class<?> currentClass = getClassFrom(stackFrame);
            if (currentClass != null && !Modifier.isAbstract(currentClass.getModifiers()) && FlowUtil.isState(currentClass)
                    && currentClass != state.getClass()) {
                return false;
            } else if (stackFrame.getClassName().equals(state.getClass().getName())) {
                return true;
            }

        }

        return false;
    }
    
    /**
     * TODO probably should pull from a cache of string,class map
     */
    protected Class<?> getClassFrom(StackTraceElement stackFrame) {
        Class<?> currentClass = null;
        try {
            String name = stackFrame.getClassName(); 
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (!name.startsWith("sun") && !name.startsWith("org.gradle") && !name.startsWith("com.sun") && !name.startsWith("jdk")) {
                currentClass = classLoader.loadClass(name);
            }
        } catch (Exception e) {
            logger.warn("", e);
        }
        return currentClass;
    }

    public void checkStackOverflow(StackTraceElement[] currentStack) {
        int stateManagerCount = 0;

        for (StackTraceElement stackFrame : currentStack) {
            // TODO count org.jumpmind.pos.core.flow.StateManager classes
            if (StateManager.class.getName().equals(stackFrame.getClassName())) {
                if (stateManagerCount++ > 300) {
                    throw new FlowException("Action cycle detected.  You may need to adjust your "
                            + "use of onAnyMethod() and/or the use of super classes for your State. A super/abstract class cannot forward an Action that it could handle.");
                }
            }
        }
    }

    public IBeforeActionService getBeforeActionService() {
        return beforeActionService;
    }

    public void setBeforeActionService(IBeforeActionService beforeActionService) {
        this.beforeActionService = beforeActionService;
    }
}
