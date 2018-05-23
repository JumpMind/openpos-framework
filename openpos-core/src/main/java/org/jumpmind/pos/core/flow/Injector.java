package org.jumpmind.pos.core.flow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

@Component
@org.springframework.context.annotation.Scope("prototype")
public class Injector {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AutowireCapableBeanFactory applicationContext;

    public void performInjections(Object target, Scope scope, StateContext currentContext) {
        performInjectionsImpl(target, scope, currentContext);
        performPostContruct(target);
    }

    protected void performInjectionsImpl(Object target, Scope scope, StateContext currentContext) {
        Class<?> targetClass = target.getClass();
        if (applicationContext != null) {
            applicationContext.autowireBean(target);
        }
        while (targetClass != null) {
            performInjectionsImpl(targetClass, target, scope, currentContext);
            targetClass = targetClass.getSuperclass();
            if (targetClass == Object.class) {
                targetClass = null;
            }
        }
    }

    protected void performInjectionsImpl(Class<?> targetClass, Object target, Scope scope, StateContext currentContext) {
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            In in = field.getAnnotation(In.class);
            if (in == null) {
                in = field.getDeclaredAnnotation(In.class);
            }

            if (in != null) {
                injectField(targetClass, target, scope, currentContext, in.name(), in.scope(), in.required(), field);
            }

            InOut inOut = field.getAnnotation(InOut.class);
            if (inOut == null) {
                inOut = field.getDeclaredAnnotation(InOut.class);
            }

            if (inOut != null) {
                injectField(targetClass, target, scope, currentContext, inOut.name(), inOut.scope(), inOut.required(), field);
            }

        }
    }

    protected void injectField(Class<?> targetClass, Object target, Scope scope, StateContext currentContext, String name,
            ScopeType scopeType, boolean required, Field field) {
        if (StringUtils.isEmpty(name)) {
            name = field.getName();
        }

        ScopeValue value = null;

        switch (scopeType) {
            case Config:
                Object configScopeValue = currentContext.getFlowConfig().getConfigScope() != null
                        ? currentContext.getFlowConfig().getConfigScope().get(name)
                        : null;
                if (configScopeValue != null) {
                    value = new ScopeValue(configScopeValue);
                }
                break;
            case Node:
                value = scope.getNodeScope().get(name);
                break;
            case Session:
                value = scope.getSessionScope().get(name);
                break;
            case Conversation:
                value = scope.getConversationScope().get(name);
                break;
            case Flow:
                value = currentContext.resolveScope(name);
                break;
            default:
                break;
        }

        if (value != null) {
            try {
                field.set(target, value.getValue());
            } catch (Exception ex) {
                logger.error("", ex);
            }
        } else if (required) {
            throw failedToResolveInjection(field, name, targetClass, target, scope, currentContext);
        }
    }

    protected void performPostContruct(Object target) {
        Method[] methods = target.getClass().getDeclaredMethods();
        for (Method method : methods) {
            PostConstruct postConstructAnnotation = method.getAnnotation(PostConstruct.class);
            if (postConstructAnnotation != null) {
                method.setAccessible(true);
                try {
                    method.invoke(target);
                } catch (Exception ex) {
                    throw new FlowException("Failed to invoke @PostConstruct method " + method, ex);
                }
            }
        }
    }

    private FlowException failedToResolveInjection(Field field, String name, Class<?> targetClass, Object target, Scope scope,
            StateContext currentContext) {

        StringBuilder buff = new StringBuilder();
        buff.append(String.format("Failed to resolve required injection '%s' for field %s\n", name, field));
        buff.append("Tried the following contexts:\n");
        buff.append(printScopeValues(scope, currentContext));

        throw new FlowException(buff.toString());
    }

    public String printScopeValues(Scope scope, StateContext currentContext) {

        StringBuilder buff = new StringBuilder();

        buff.append(reportScope("NODE SCOPE", scope.getNodeScope()));
        buff.append(reportScope("SESSION SCOPE", scope.getSessionScope()));
        buff.append(reportScope("CONVERSATION SCOPE", scope.getConversationScope()));
        buff.append(reportScope("FLOW SCOPE", currentContext.getFlowScope()));

        return buff.toString();
    }

    protected String reportScope(String scopeName, Map<String, ScopeValue> scope) {

        final int MAX_VALUE_WIDTH = 64;

        StringBuilder buff = new StringBuilder();

        buff.append(scopeName).append(":\n");
        if (scope != null) {
            if (scope.isEmpty()) {
                buff.append("\t<empty>\n");
            } else {
                for (Map.Entry<String, ScopeValue> entry : scope.entrySet()) {
                    buff.append("\t").append(entry.getKey()).append("=")
                            .append(StringUtils.abbreviate(entry.getValue().getValue().toString(), MAX_VALUE_WIDTH)).append("\n");
                }
            }
        }

        return buff.toString();
    }
}
