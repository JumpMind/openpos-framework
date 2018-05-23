package org.jumpmind.pos.core.flow;

import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@org.springframework.context.annotation.Scope("prototype")
public class Outjector {

    public void performOutjections(Object target, Scope scope, StateContext currentContext) {
        Class<?> targetClass = target.getClass();
        while (targetClass != null) {
            performOutjectionsImpl(targetClass, target, scope, currentContext);
            targetClass = targetClass.getSuperclass();
            if (targetClass == Object.class) {
                targetClass = null;
            }
        }
    }

    protected void performOutjectionsImpl(Class<?> targetClass, Object target, Scope scope, StateContext currentContext) {
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Out out = field.getAnnotation(Out.class);
            if (out != null) {
                outjectField(target, scope, currentContext, out.name(), out.scope(), out.required(), field);
            }
            
            InOut inOut = field.getAnnotation(InOut.class);
            if (inOut != null) {
                outjectField(target, scope, currentContext, inOut.name(), inOut.scope(), inOut.required(), field);
            }
        }
    }

    protected void outjectField(Object target, Scope scope, StateContext currentContext, String name, ScopeType scopeType, boolean required,
            Field field) {
        if (StringUtils.isEmpty(name)) {
            name = field.getName();
        }

        try {
            Object value = field.get(target);
            if (value == null && required) {
                throw new FlowException("Required outjection " + name + " is null, but should be non-null for field " + field);
            }

            switch (scopeType) {
                case Node:
                    scope.setNodeScope(name, value);
                    break;
                case Session:
                    scope.setSessionScope(name, value);
                    break;
                case Conversation:
                    scope.setConversationScope(name, value);
                    break;
                case Flow:
                    currentContext.setFlowScope(name, value);
                    break;
                default:
                    throw new FlowException("Invalid scope " + scopeType + " for out field " + field);
            }
        } catch (Exception ex) {
            if (ex instanceof FlowException) {
                throw (FlowException) ex;
            } else {
                throw new FlowException("Could not process outjection for field " + field, ex);
            }
        }
    }
}
