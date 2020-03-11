package org.jumpmind.pos.core.flow;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.server.model.Action;
import org.springframework.stereotype.Component;

@Component
public class StateLifecycle {
    
    @SuppressWarnings("deprecation")
    public void executeArrive(StateManager stateManager, Object state, Action action) {
        try {
            boolean arrivedInvoked = false;
            if (state instanceof IState) {
                ((IState)state).arrive(action);
                arrivedInvoked = true;
            }
            arrivedInvoked |= invokeArrive(stateManager, state, action);

            if (!arrivedInvoked) {
                throw new FlowException("A state must have at least one @OnArrive method. state=" + state + " action=" + action);
            }
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
    
    protected boolean invokeArrive(StateManager stateManager, Object state, Action action) {
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(state.getClass(), OnArrive.class, true, true);
        boolean arrivedCalled = false;
        Set<String> called = new HashSet<>();
        if (methods != null && !methods.isEmpty()) {
            for (Method method : methods) {
                if (!called.contains(method.getName())) {
                    invokeLifecyleMethod(state, action, method);
                    called.add(method.getName());
                    arrivedCalled = true;
                }
            }
        }

        return arrivedCalled;
    }    

    protected void invokeLifecyleMethod(Object state, Action action, Method method) {
        try {
            method.setAccessible(true);
            if (method.getParameters() != null && method.getParameters().length == 1) {                        
                method.invoke(state, action);
            } else {
                method.invoke(state);
            }
        } catch (Exception ex) {
            throw new FlowException("Failed to execute method on state. Method: " + method + " state: " + state, ex);
        }
    }
}
