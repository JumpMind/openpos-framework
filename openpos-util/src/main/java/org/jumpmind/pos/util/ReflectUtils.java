package org.jumpmind.pos.util;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import static org.apache.commons.beanutils.BeanUtils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static java.lang.String.*;
import org.apache.commons.lang3.time.DateUtils;

@Slf4j
public class ReflectUtils {

    static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);

    public static void setProperty(Field field, Object target, Object value) {
        value = messageNulls(field, value);
        try {
            Object coercedValue = coerceValue(field, value);

            try {
                field.set(target, coercedValue);
                return;
            } catch (Exception ex) {
                try {
                    copyProperty(target, field.getName(), value);
                    return;
                } catch (Exception ex2) {
                    throw ex;
                }
            }
        } catch (Exception ex) {
            throw new ReflectionException(String.format("Failed to set field '%s' on target '%s' to value '%s'", field.getName(), target, value),
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

        if (field.getType().equals(Date.class)) {
            if (value instanceof Number) {
                value = value.toString();
            }

            if (value instanceof String) {
                try {
                    value = DateUtils.parseDate((String) value, "yyyyMMdd", "yyyyMMdd hh:mm:ss", "yyyy-MM-dd hh:mm:ss.SSS");
                } catch (ParseException e) {
                    throw new ReflectionException("Failed to parse this string " + value + " to a date value.  You might need to add a new date pattern to the list", e);
                }
            }
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

    public static void setProperty(Object target, String propertyName, Object value) {
        setProperty(target, propertyName, value, false);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void setProperty(Object target, String propertyName, Object value, boolean ignoreIfFieldNotFound) {
        Field field = getAccessibleField(target, propertyName);
        if (field != null) {
            setProperty(field, target, value);
        } else if (!ignoreIfFieldNotFound) {
            throw new ReflectionException("Did not find %s on the target class of %s", propertyName, target.getClass().getName());
        } else {
            log.debug("Did not find {}} on the target class of {}}", propertyName, target.getClass().getName());
        }
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
