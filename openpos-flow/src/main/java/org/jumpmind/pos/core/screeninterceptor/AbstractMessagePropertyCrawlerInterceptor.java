package org.jumpmind.pos.core.screeninterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jumpmind.pos.core.flow.IMessageInterceptor;
import org.jumpmind.pos.util.model.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractMessagePropertyCrawlerInterceptor<T extends Message> implements IMessageInterceptor<T> {

    Logger logger = LoggerFactory.getLogger(getClass());
    
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }
    
    public abstract List<IMessagePropertyStrategy<T>> getMessagePropertyStrategies();
    public abstract void setMessagePropertyStrategies(List<IMessagePropertyStrategy<T>> strategies);
    
    @Override
    public void intercept(String appId, String deviceId, T message) {
        Map<String, Object> messageContext = new HashMap<>();
        processFields(appId, deviceId, message, message, messageContext);
    }

    private final void processFields(String appId, String deviceId, Object obj, T message, Map<String, Object> messageContext) {
        Class<?> clazz = obj.getClass();
        while (clazz != null && obj != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                try {
                    Object value = field.get(obj);

                    try {
                        if (!Modifier.isFinal(field.getModifiers())) {
                            field.set(obj, doStrategies(appId, deviceId, field, obj, message, messageContext));
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        logger.error("Failed to set property value", e);
                    }

                    if (!processCollections(appId, deviceId, value, message, messageContext) && shouldProcess(field) && shouldProcess(type)) {

                        if (value != null) {
                            processFields(appId, deviceId, value, message, messageContext);
                        }

                    }

                } catch (Exception e) {
                    logger.warn("", e);
                }
            }
            clazz = clazz.getSuperclass();
        }

    }

    private boolean processCollections(String appId, String deviceId, Object value, T message, Map<String, Object> messageContext) {
        if (value instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                Object fieldObj = list.get(i);
                if (fieldObj != null) {
                    list.set(i, doStrategies(appId, deviceId, fieldObj, fieldObj.getClass(), message, messageContext));
                    if (!processCollections(appId, deviceId, fieldObj, message, messageContext) && shouldProcess(fieldObj.getClass())) {
                        processFields(appId, deviceId, fieldObj, message, messageContext);
                    }
                }
            }
            return true;

        } else if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) value;
            Iterator<?> i = collection.iterator();
            while (i.hasNext()) {
                Object fieldObj = i.next();
                if (fieldObj != null && !processCollections(appId, deviceId, fieldObj, message, messageContext)
                        && shouldProcess(fieldObj.getClass())) {
                    processFields(appId, deviceId, fieldObj, message, messageContext);
                }
            }
            return true;

        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) value;
            for (Entry<Object, Object> entry : map.entrySet()) {
                Object entryValue = entry.getValue();
                if (entryValue != null) {
                    entry.setValue(doStrategies(appId, deviceId, entryValue, entryValue.getClass(), message, messageContext));
                    if (entryValue != null && !processCollections(appId, deviceId, entryValue, message, messageContext)
                            && shouldProcess(entryValue.getClass())) {
                        processFields(appId, deviceId, entryValue, message, messageContext);
                    }
                }
            }
            return true;

        } else {
            return false;
        }
    }

    private Object doStrategies(String appId, String deviceId, Field field, Object obj, T message, Map<String, Object> messageContext) {
        try {
            Object property = field.get(obj);
            Class<?> clazz = (property != null ? property.getClass() : field.getType());
            return doStrategies(appId, deviceId, property, clazz, message, messageContext);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            logger.error("Failed to crawl message property", e);
        }
        return obj;
    }

    private Object doStrategies(
            String appId,
            String deviceId,
            Object property,
            Class<?> clazz,
            T message,
            Map<String, Object> messageContext) {
        List<IMessagePropertyStrategy<T>> strategies = this.getMessagePropertyStrategies();
        if (strategies != null) {
            for (IMessagePropertyStrategy<T> s : strategies) {
                property = s.doStrategy(appId, deviceId, property, clazz, message, messageContext);
            }
        }
        return property;
    }

    private boolean shouldProcess(Field field) {
        return !Modifier.isStatic(field.getModifiers());
    }

    private boolean shouldProcess(Class<?> clazz) {
        return clazz != null && !isWrapperType(clazz) && !clazz.isPrimitive() && !clazz.isEnum() && !clazz.equals(Logger.class)
                && clazz.getPackage() != null && !clazz.getPackage().getName().startsWith("sun");
    }


}
