package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;

import org.apache.commons.beanutils.BeanUtils;
import org.jumpmind.pos.persist.PersistException;

public class ReflectUtils {

    public static void setProperty(Field field, Object target, Object value) {
        value = messageNulls(field, value);
        try {
            Object coercedValue = coerceValue(field, value);

            try {
                field.set(target, coercedValue);
                return;
            } catch (Exception ex) {
                try {
                    BeanUtils.copyProperty(target, field.getName(), value);
                    return;
                } catch (Exception ex2) {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            throw new PersistException(String.format("Failed to set field '%s' on target '%s' to value '%s'", field.getName(), target, value),
                    ex);
        }
    }

    private static Object coerceValue(Field field, Object value) {
        if (value == null) {
            return null;
        }

        if (field.getType().isEnum()) {
            return Enum.valueOf((Class<Enum>) field.getType(), value.toString());
        }

        if ((field.getType().equals(Boolean.class) || field.getType().equals(boolean.class))
            && (value instanceof Number)    ) {
            Number number = (Number) value;
            if (number.intValue() != 0) {
                return Boolean.TRUE;
            } else {
                return Boolean.FALSE;
            }
        }

        return value;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setProperty(Object target, String propertyName, Object value) {
        Field field = getAccessibleField(target, propertyName);
        setProperty(field, target, value);
    }

    private static Object messageNulls(Field field, Object value) {
        if (value != null) {
            return value;
        }
        if (field != null && field.getType().isPrimitive()) {
            return 0;
        }

        return value;
    }

    public static Field getAccessibleField(Object target, String propertyName) {
        Class<? extends Object> clazz = target.getClass();
        while (clazz != Object.class) {
            Field field = null;
            try {
                field = clazz.getDeclaredField(propertyName);
            } catch (NoSuchFieldException ex2) {
            }
            if (field != null) {
                field.setAccessible(true);
                return field;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

}
