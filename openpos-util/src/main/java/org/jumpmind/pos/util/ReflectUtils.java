package org.jumpmind.pos.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.mutable.MutableLong;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.apache.commons.beanutils.BeanUtils.copyProperty;

@Slf4j
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
        } else if (field.getType().equals(Integer.class) && value instanceof Number) {
            value = ((Number) value).intValue();
        } else if (field.getType().equals(BigDecimal.class) && value instanceof Number) {
            value = new BigDecimal(value.toString());
        } else if ((field.getType().equals(MutableLong.class) || field.getType().equals(Long.class)) && value instanceof Number) {
            value = ((Number) value).longValue();
        } else if ((field.getType().equals(Boolean.class) || field.getType().equals(boolean.class))
                && (value instanceof Number)) {
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

    @SuppressWarnings({"unchecked", "rawtypes"})
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

    public static Method getAccessibleMethod(Object target, String methodName) {
        Class<? extends Object> clazz = target.getClass();
        while (clazz != Object.class) {
            Method method = null;
            try {
                method = clazz.getDeclaredMethod(methodName);
            } catch (NoSuchMethodException e) {
            }
            if (method != null) {
                method.setAccessible(true);
                return method;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    public static PropertyDescriptor getPropertyDescriptor(Object target, String propertyName) {
        try {
            Class<? extends Object> clazz = target.getClass();
            while (clazz != Object.class) {
                BeanInfo clazzInfo = Introspector.getBeanInfo(clazz);
                List<PropertyDescriptor> properties = Arrays.asList(clazzInfo.getPropertyDescriptors());
                PropertyDescriptor property = properties.stream().filter(p -> p.getName().equals(propertyName)).findFirst().orElse(null);
                if (property != null) {
                    return property;
                }
                clazz = clazz.getSuperclass();
            }
            return null;
        } catch (Exception ex) {
            throw new ReflectionException("Failed to execute getPropertyDescriptor for property '" + propertyName + "' on " + target, ex);
        }
    }


    /**
     * This simple method will convert any input string into a valid java identifier
     */
    public static String toIdentifier(String str) {
        if (str != null) {
            // TODO this will need to become more advanced in the future
            str = str.replaceAll("-", "");
        }
        return str;
    }

}
