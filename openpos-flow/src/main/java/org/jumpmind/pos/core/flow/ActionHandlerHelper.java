package org.jumpmind.pos.core.flow;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.server.model.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides methods that can be shared for resolving and executing {@link ActionHandler)
 * methods on a given object.
 */
@Component
public class ActionHandlerHelper {
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected static final String METHOD_ON_ANY = "onAnyAction";

    @Autowired(required = false)
    protected IErrorHandler errorHandler;

    
    public void checkStackOverflow(Class<?> classToCount, Object handler, StackTraceElement[] currentStack) {
        int stateManagerCount = 0;

        for (StackTraceElement stackFrame : currentStack) {
            if (classToCount.getName().equals(stackFrame.getClassName())) {
                if (stateManagerCount++ > 300) {
                    throw new FlowException("Action cycle detected.  You may need to adjust your "
                            + "use of onAnyMethod() and/or the use of super classes for " 
                            + handler.getClass().getSimpleName() 
                            + ". A super/abstract class cannot forward an Action that it could handle.");
                }
            }
        }
    }

    public void invokeActionMethod(IStateManager stateManager, Object targetObj, Action action, Method method, Object...otherArgs) {
        method.setAccessible(true);
        List<Object> arguments = new ArrayList<Object>();
        try {
            int i = 0;
            for (Class<?> type : method.getParameterTypes()) {
                i++;
                logger.trace("Processing parameter {} of type: {}", i, type.getSimpleName());
                if (type.isAssignableFrom(Action.class)) {
                    arguments.add(action);
                } else if (action.getData() != null && type.isAssignableFrom(action.getData().getClass())) {
                    arguments.add(action.getData());
                } else {
                    if (otherArgs != null) {
                        // Assumes otherArgs doesn't have any duplicated types and that
                        // no two arguments in otherArgs could both be assigned to
                        // the current type
                        arguments.add(Arrays.stream(otherArgs).filter(o -> o != null && type.isAssignableFrom(o.getClass())).findFirst().orElse(null));
                    } else {
                        arguments.add(null);
                    }
                }
            }

            method.invoke(targetObj, arguments.toArray(new Object[arguments.size()]));
        } catch (Exception ex) {
            if (errorHandler != null) {
                errorHandler.handleError(stateManager, ex);
            } else {
                throw new FlowException("Failed to invoke method " + method, ex);
            }
        }
    }

    /**
     * TODO probably should pull from a cache of string,class map
     */
    public Class<?> getClassFrom(StackTraceElement stackFrame) {
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
    
    public Method getAnyActionMethod(Object targetObj) {
        Class<?> clazz = targetObj.getClass();

        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, ActionHandler.class, true, true);

        for (Method method : methods) {
            if (METHOD_ON_ANY.equals(method.getName())) {
                return method;
            }
        }

        return null;
    }

    public Method getActionMethod(Object targetObj, Action action) {
        Class<?> clazz = targetObj.getClass();

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
    
}
