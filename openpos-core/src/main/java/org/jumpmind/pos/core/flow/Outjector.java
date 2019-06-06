package org.jumpmind.pos.core.flow;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@org.springframework.context.annotation.Scope("prototype")
public class Outjector {

    public void performOutjections(Object target, Scope scope, StateContext currentContext) {
        
        Map<String, OutjectedValue> outjectedValues = new HashMap<>();
        
        Class<?> targetClass = target.getClass();
        while (targetClass != null) {
            performOutjectionsImpl(targetClass, target, scope, currentContext, outjectedValues);
            targetClass = targetClass.getSuperclass();
            if (targetClass == Object.class) {
                targetClass = null;
            }
        }
    }

    protected void performOutjectionsImpl(Class<?> targetClass, Object target, Scope scope, StateContext currentContext, Map<String, OutjectedValue> outjectedValues) {
        Field[] fields = targetClass.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Out out = field.getAnnotation(Out.class);
            if (out != null) {
                outjectField(target, scope, currentContext, out.name(), out.scope(), out.required(), field, outjectedValues);
            }
            
            InOut inOut = field.getAnnotation(InOut.class);
            if (inOut != null) {
                outjectField(target, scope, currentContext, inOut.name(), inOut.scope(), inOut.required(), field, outjectedValues);
            }
        }
    }

    protected void outjectField(Object target, Scope scope, StateContext currentContext, String name, ScopeType scopeType, boolean required,
            Field field, Map<String, OutjectedValue> outjectedValues) {
        if (StringUtils.isEmpty(name)) {
            name = field.getName();
        }

        try {
            Object value = field.get(target);
            if (value == null && required) {
                throw new FlowException("Required outjection " + name + " is null, but should be non-null for field " + field);
            }
            
            validateFieldValueConsistency(name, field, value, outjectedValues);

            switch (scopeType) {
                case Device:
                    scope.setDeviceScope(name, value);
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

    protected void validateFieldValueConsistency(String name, Field field, Object value, Map<String, OutjectedValue> outjectedValues) {
        if (outjectedValues.containsKey(name)) {
            OutjectedValue oujectedValue = outjectedValues.get(name);
            if (!Objects.equals(value, oujectedValue.getValue())) {
                throw new FlowException(String.format("Cannot perform outjection of field '%s' reliably. The outjected field was found in a base class and a subclass, "
                        + "and the field has different values in the base class and subclass. "
                        + "Consider making the base class field protected and removing the subclass' copy of this field. base class field:%s=%s, subclass field:%s=%s", 
                        name, field, value, 
                        oujectedValue.getField(), oujectedValue.getValue()));
            }
        }
        
        outjectedValues.put(name, new OutjectedValue(field, value));
    }
}
