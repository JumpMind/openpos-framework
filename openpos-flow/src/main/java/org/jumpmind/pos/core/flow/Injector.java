package org.jumpmind.pos.core.flow;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.collections4.CollectionUtils;
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

    @Autowired(required = false)
    private List<IScopeValueProvider> scopeValueProviders;

    private final static boolean AUTOWIRE = true;
    private final static boolean DONT_AUTOWIRE = false;

    public void performInjections(Object target, Scope scope, StateContext currentContext) {
        performInjectionsImpl(target, scope, currentContext, AUTOWIRE);
        performPostContruct(target);
    }

    public void performInjectionsOnSpringBean(Object target, Scope scope, StateContext currentContext) {
        performInjectionsImpl(target, scope, currentContext, DONT_AUTOWIRE);
    }

    public void resetInjections(Object target, ScopeType scopeType) {
        Class<?> targetClass = target != null ? target.getClass() : null;
        while (targetClass != null) {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                boolean nullField = false;
                In in = field.getAnnotation(In.class);
                if (in == null) {
                    in = field.getDeclaredAnnotation(In.class);
                }

                if (in != null && in.scope() == scopeType) {
                    nullField = true;
                }

                InOut inOut = field.getAnnotation(InOut.class);
                if (inOut == null) {
                    inOut = field.getDeclaredAnnotation(InOut.class);
                }

                if (inOut != null && inOut.scope() == scopeType) {
                    nullField = true;
                }

                if (nullField) {
                    try {
                        if (!field.getType().isPrimitive()) {                        
                            field.set(target, null);
                        } else if (field.getType().equals(int.class)){
                            field.set(target, 0);
                        } else if (field.getType().equals(boolean.class)){
                            field.set(target, false);
                        } else {
                            throw new FlowException("Unhandled type: " + field.getType() + " on target " + target);
                        }
                    } catch (Exception ex) {
                        throw new FlowException("Failed to reset target field " + field + " to null/0", ex);
                    }
                }
            }
            targetClass = targetClass.getSuperclass();
            if (targetClass == Object.class) {
                targetClass = null;
            }
        }
    }

    protected void performInjectionsImpl(Object target, Scope scope, StateContext currentContext, boolean autowire) {
        Class<?> targetClass = target != null ? target.getClass() : null;
        if (autowire && applicationContext != null && targetClass != null) {
            logger.trace("Running Spring Autowiring on '{}'...", targetClass.getName());
            applicationContext.autowireBean(target);
            logger.trace("Spring Autowiring on '{}' completed", targetClass.getName());
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
                injectField(targetClass, target, scope, currentContext, in.name(), in.scope(), in.required(), in.autoCreate(), field);
            }

            InOut inOut = field.getAnnotation(InOut.class);
            if (inOut == null) {
                inOut = field.getDeclaredAnnotation(InOut.class);
            }

            if (inOut != null) {
                injectField(targetClass, target, scope, currentContext, inOut.name(), inOut.scope(), inOut.required(), inOut.autoCreate(),
                        field);
            }
        }
    }    

    protected void injectField(Class<?> targetClass, Object target, Scope scope, StateContext currentContext, String name,
            ScopeType scopeType, boolean required, boolean autoCreate, Field field) {
        if (StringUtils.isEmpty(name)) {
            name = field.getName();
        }

        logger.trace("Injecting field '{}' on bean {}...", name, target);
        ScopeValue value = null;

        switch (scopeType) {
            case Config:
                Object configScopeValue = currentContext.getFlowConfig().getConfigScope() != null
                ? currentContext.getFlowConfig().getConfigScope().get(name)
                        : null;
                value = new ScopeValue(configScopeValue);
                break;
            case Device:
                value = scope.getDeviceScope().get(name);
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

        if ((value == null || value.getValue() == null) && autoCreate) {
            value = autoCreate(name, scopeType, scope, currentContext);
        }

        if (value == null) {
            value = resolveThroughValueProviders(name, scopeType, target, field);
            if (value != null) {
                scope.setScopeValue(scopeType, name, value);
            }            
        }

        
        if( (value == null || value.getValue() == null) && required ){
            throw failedToResolveInjection(field, name, targetClass, target, scope, currentContext, scopeType);
        } else if ((value != null && value.getValue() != null) || (ScopeType.Config.equals(scopeType) && !field.getType().isPrimitive())) {
            try {
                field.set(target, value.getValue());
                logger.trace("Injected field '{}' with value {}", name, value.getValue());
            } catch (Exception ex) {
                throw new FlowException("Failed to set target field " + field + " to value " + value.getValue(), ex);
            }
        }
        logger.trace("Injection of field '{}' on bean {} completed", name, target);
    }
    
    protected ScopeValue autoCreate(String name, ScopeType scopeType, Scope scope, StateContext currentContext) {
        Object bean = applicationContext.getBean(name);
        performInjections(bean, scope, currentContext);
        scope.setScopeValue(scopeType, name, bean);
        return new ScopeValue(bean);
    }

    protected ScopeValue resolveThroughValueProviders(String name, ScopeType scopeType, Object target, Field field) {
        ScopeValue value = null;
        if (!CollectionUtils.isEmpty(scopeValueProviders)) {
            for (IScopeValueProvider valueProvider : scopeValueProviders) {
                value = valueProvider.getValue(name, scopeType, target, field);
                if (value != null) {
                    break;
                }
            }
        }
        return value;
    }

    protected void performPostContruct(Object target) {
        Method[] methods = target != null ? target.getClass().getDeclaredMethods() : new Method[0];
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

    private FlowException failedToResolveInjection(
            Field field,
            String name,
            Class<?> targetClass,
            Object target,
            Scope scope,
            StateContext currentContext, ScopeType scopeType) {

        StringBuilder buff = new StringBuilder();
        buff.append(String.format("Failed to resolve required injection '%s' for field %s at scope %s\n", name, field, scopeType));
        buff.append("The following values are in scope:\n");
        buff.append(printScopeValues(scope, currentContext));

        throw new FlowException(buff.toString());
    }

    public String printScopeValues(Scope scope, StateContext currentContext) {

        StringBuilder buff = new StringBuilder();

        try {
            buff.append(reportScope("DEVICE SCOPE", scope.getDeviceScope()));
            buff.append(reportScope("SESSION SCOPE", scope.getSessionScope()));
            buff.append(reportScope("CONVERSATION SCOPE", scope.getConversationScope()));
            buff.append(reportScope("FLOW SCOPE", currentContext != null ? currentContext.getFlowScope() : null));
        } catch (Exception ex) {
            logger.warn("Exception while generating scope report", ex);
        }

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
                    buff.append("\t").append(entry.getKey()).append("=");
                    if (entry.getValue() == null || entry.getValue().getValue() == null) {
                        buff.append("null").append("\n");
                    } else {
                        buff.append(StringUtils.abbreviate(entry.getValue().getValue().toString(), MAX_VALUE_WIDTH)).append("\n");
                    }
                }
            }
        }

        return buff.toString();
    }

    public boolean hasInjections(Object bean) {
        Class<?> targetClass = bean.getClass();
        while (targetClass != null) {
            Field[] fields = targetClass.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                In in = field.getAnnotation(In.class);
                if (in == null) {
                    in = field.getDeclaredAnnotation(In.class);
                }

                if (in != null) {
                    return true;
                }

                InOut inOut = field.getAnnotation(InOut.class);
                if (inOut == null) {
                    inOut = field.getDeclaredAnnotation(InOut.class);
                }

                if (inOut != null) {
                    return true;
                }
            }
            targetClass = targetClass.getSuperclass();
            if (targetClass == Object.class) {
                targetClass = null;
            }
        }
        
        return false;
    }
}
