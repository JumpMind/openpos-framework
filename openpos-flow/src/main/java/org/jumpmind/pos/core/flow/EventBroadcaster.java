package org.jumpmind.pos.core.flow;

import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.Event;
import org.jumpmind.pos.util.event.OnEvent;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

public class EventBroadcaster {

    IStateManager stateManager;

    public EventBroadcaster(IStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public void postEventToObject(Class clazz, Object object, Event event) {
        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, OnEvent.class, true, true);
        if (methods != null && !methods.isEmpty()) {
            if (object == null) {
                try {
                    object = clazz.newInstance();
                    stateManager.performInjections(object);
                } catch (InstantiationException | IllegalAccessException ex) {
                    throw new FlowException("Failed to create event handler of type " + clazz.getName(), ex);
                } catch (Exception ex) {
                    throw new FlowException("Failed to inject values on the event handler of type " + clazz.getName(), ex);
                }
            }
            for (Method method : methods) {
                try {
                    OnEvent onEvent = method.getAnnotation(OnEvent.class);
                    if (onEvent.receiveEventsFromSelf() || !event.getSource().equals(AppEvent.createSourceString(stateManager.getAppId(), stateManager.getDeviceId()))) {
                        if (method.getParameters() != null && method.getParameters().length == 1 && method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                            method.setAccessible(true);
                            method.invoke(object, event);
                        } else if (method.getParameters() == null || method.getParameters().length == 0) {
                            method.setAccessible(true);
                            method.invoke(object);
                        }
                    }
                } catch (Exception ex) {
                    throw new FlowException("Failed to execute method on state. Method: " + method + " object: " + object, ex);
                }
            }
        }
    }
}
