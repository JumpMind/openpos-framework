package org.jumpmind.pos.core.flow;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.Event;
import org.jumpmind.pos.util.event.OnEvent;

import java.lang.reflect.Method;
import java.util.List;

public class EventBroadcaster {

    IStateManager stateManager;

    public EventBroadcaster(IStateManager stateManager) {
        this.stateManager = stateManager;
    }

    public boolean postEventToObject(Class clazz, Event event) {
        return postEventToObject(clazz, null, event);
    }

    public boolean postEventToObject(Object object, Event event) {
        if (object instanceof ScopeValue) {
            ScopeValue scopeValue = (ScopeValue)object;
            object = scopeValue.getValue();
        }
        if (object != null) {
            return postEventToObject(object.getClass(), object, event);
        } else {
            return false;
        }
    }

    public boolean postEventToObject(Class clazz, Object object, Event event) {
        boolean foundMethod = true;

        List<Method> methods = MethodUtils.getMethodsListWithAnnotation(clazz, OnEvent.class, true, true);
        if (methods.size() > 0) {
            for (Method method : methods) {
                boolean processEvent = false;
                try {
                    OnEvent onEvent = method.getAnnotation(OnEvent.class);
                    if (onEvent.receiveAllEvents() || ArrayUtils.contains(onEvent.ofTypes(), event.getClass())) {
                        processEvent = true;
                    } else {
                        // if an appevent then only receive events from the same device id
                        if (!(event instanceof AppEvent) || stateManager.getDeviceId().equals(((AppEvent) event).getDeviceId())) {
                            String mySource = AppEvent.createSourceString(stateManager.getAppId(), stateManager.getDeviceId(), stateManager.getPairedDeviceId());

                            if (onEvent.receiveEventsFromSelf() || !event.getSource().equals(mySource)) {
                                processEvent = true;
                            }
                        }
                        if (event instanceof AppEvent && onEvent.receiveEventsFromPairedDevice() &&
                                (stateManager.getDeviceId().equals(((AppEvent) event).getPairedDeviceId()) ||
                                        ((AppEvent) event).getDeviceId().equals(stateManager.getPairedDeviceId()))) {
                            processEvent = true;
                        }
                    }
                } catch (Exception ex) {
                    throw new FlowException("Failed to execute method on state. Method: " + method + " object: " + object, ex);
                }

                if (processEvent) {
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

                    try {
                        method.setAccessible(true);
                        foundMethod = true;
                        if (method.getParameters() != null && method.getParameters().length == 1 && method.getParameterTypes()[0].isAssignableFrom(event.getClass())) {
                            method.invoke(object, event);
                        } else if (method.getParameters() == null || method.getParameters().length == 0) {
                            method.invoke(object);
                        }
                    } catch (Exception ex) {
                        throw new FlowException("Failed to execute method on state. Method: " + method + " object: " + object, ex);
                    }
                }
            }
        } else {
            foundMethod = false;
        }
        return foundMethod;

    }
}
