package org.jumpmind.pos.core.flow;

import java.lang.reflect.Method;
import java.util.List;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.server.model.Action;
import org.springframework.stereotype.Component;

@Component
public class StateLifecycle {
    
    @SuppressWarnings("deprecation")
    public void executeArrive(StateManager stateManager, Object state, Action action) {
        try {
            if (state instanceof IState) {
                ((IState)state).arrive(action);  
            }
            invokeArrive(stateManager, state, action);
        } catch (RuntimeException ex) {
            if (stateManager.getErrorHandler() != null) {
                stateManager.getErrorHandler().handleError(stateManager, ex);
            } else {
                throw ex;
            }
        }
    }

    public void executeDepart(Object oldState, Object newState, boolean enterSubState, Action action) {
        if (oldState == null) {
            return;
        }

        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(oldState.getClass(), OnDepart.class, true, true);
        if (methods != null && !methods.isEmpty()) {
            for (Method method : methods) {
                OnDepart onDepart = method.getAnnotation(OnDepart.class);
                if (enterSubState && onDepart.toSubflow()) {
                    invokeLifecyleMethod(oldState, action, method);
                } else if (!enterSubState && onDepart.toAnotherState() && oldState != newState) {                    
                    invokeLifecyleMethod(oldState, action, method);
                }
            }
        }
    }

    public void executeFlowEvent(Object state, FlowEvent event) {
        if (state == null) {
            return;
        }

        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(state.getClass(), OnEvent.class, true, true);
        if (methods != null && !methods.isEmpty()) {
            for (Method method : methods) {
                OnEvent onEvent = method.getAnnotation(OnEvent.class);
                invokeMethod(state, event, method);
            }
        }
    }
    
    protected void invokeArrive(StateManager stateManager, Object state, Action action) {
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(state.getClass(), OnArrive.class, true, true);
        if (methods != null && !methods.isEmpty()) {
            for (Method method : methods) {        
                invokeLifecyleMethod(state, action, method);   
            }
        }
    }    

    protected void invokeLifecyleMethod(Object state, Action action, Method method) {
        invokeMethod(state, action, method);
    }

    protected void invokeMethod(Object state, Object arg, Method method) {
        try {
            if (method.getParameters() != null && method.getParameters().length == 1) {
                method.invoke(state, arg);
            } else {
                method.invoke(state);
            }
        } catch (Exception ex) {
            throw new FlowException("Failed to execute method on state. Method: " + method + " state: " + state, ex);
        }
    }
}
