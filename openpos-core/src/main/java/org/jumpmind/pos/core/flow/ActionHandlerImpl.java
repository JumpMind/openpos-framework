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
        // AND it's not the current state fireing this action.
        Method actionMethod = getActionMethod(state, action, null, false);
        if  (actionMethod != null
                && !isCalledFromState(state)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean handleAction(Object state, Action action, Object deserializedPayload, String overrideActionName) {
        Method actionMethod = getActionMethod(state, action, overrideActionName, true);
        if (actionMethod != null) {
            invokeHandleAction(state, action, actionMethod, deserializedPayload);
            return true;
        } else {
            return false;
        }
    }
    
    protected Method getActionMethod(Object state, Action action, String overrideActionName, boolean considerOnAnyActionMethod) {
        Class<?> clazz = state.getClass();
        
        Method anyMethod = null;

        while (clazz != null) {
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
                } else if (METHOD_ON_ANY.equals(method.getName())) {
                    anyMethod = method;
                }
            }
            clazz = clazz.getSuperclass();
        }
        
        if (anyMethod != null && considerOnAnyActionMethod) {
            return anyMethod;
        } else {            
            return null;
        }
    }

    protected void invokeHandleAction(Object state, Action action, Method method, Object deserializedPayload) {
        List<Object> arguments = new ArrayList<Object>();
        try {
            for (Class<?> type : method.getParameterTypes()) {
                if (type.isAssignableFrom(Action.class)) {
                    arguments.add(action);
                } else if (deserializedPayload != null && type.isAssignableFrom(deserializedPayload.getClass())) {
                    arguments.add(deserializedPayload);
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
