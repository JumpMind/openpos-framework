package org.jumpmind.pos.core.flow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.stereotype.Component;

// TODO should be called just ActionHandler, need to repackage annotation of the same name.
@Component
@org.springframework.context.annotation.Scope("prototype")
public class ActionHandlerImpl {
    
    private static final String METHOD_ON_ANY = "onAnyAction";

    
    public boolean canHandleAction(Object state, Action action) {
        // if there is an action handler OR an any action handler
        // AND it's not the current state firing this action.
        Method actionMethod = getActionMethod(state, action, null);
        if  (actionMethod != null
                && !isCalledFromState(state)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean handleAction(Object state, Action action, String overrideActionName) {        
        Method actionMethod = getActionMethod(state, action, overrideActionName);
        if (actionMethod != null) {
            invokeActionMethod(state, action, actionMethod);
            return true;
        } else {
            return false;
        }
    }

    public boolean canHandleAnyAction(Object state) {
        return getAnyActionMethod(state) != null;
    }
    
    public boolean handleAnyAction(Object state, Action action) {
        Method anyActionMethod = getAnyActionMethod(state);
        if (anyActionMethod != null) {
            invokeActionMethod(state, action, anyActionMethod);
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
    
    protected Method getActionMethod(Object state, Action action, String overrideActionName) {
        Class<?> clazz = state.getClass();
        
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, ActionHandler.class, true, true);

        String actionName = null;
        if (!StringUtils.isEmpty(overrideActionName)) {
            actionName = overrideActionName;
        } else {
            actionName = action.getName();                
        }
        
        for (Method method : methods) {
            String matchingMethodName = "on" + actionName;
            method.setAccessible(true);
            if (matchingMethodName.equals(method.getName())) {
                return method;
            }
        }
        
        return null;
    }

    protected void invokeActionMethod(Object state, Action action, Method method) {
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
            throw new FlowException("Failed to invoke method " + method, ex);
        }
    }
    
    protected boolean isCalledFromState(Object state) {
        StackTraceElement[] currentStack = Thread.currentThread().getStackTrace();
        for (StackTraceElement stackFrame : currentStack) {
            if (stackFrame.getClassName().equals(state.getClass().getName())) {                
                return true;
            }
        }
        return false;
    }    

}
