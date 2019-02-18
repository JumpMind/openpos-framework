package org.jumpmind.pos.core.screeninterceptor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jumpmind.pos.core.flow.IScreenInterceptor;
import org.jumpmind.pos.core.screen.Screen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScreenPropertyCrawlerInterceptor implements IScreenInterceptor {

    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    List<IScreenPropertyStrategy> screenProprtyStrategies;

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

    @Override
    public void intercept(String appId, String deviceId, Screen screen) {
        if (screen != null && screenProprtyStrategies != null && screenProprtyStrategies.size() > 0) {
            processFields(appId, deviceId, screen, screen);
            processOptionals(appId, deviceId, screen.any(), screen);
        }
    }

    private void processOptionals(String appId, String deviceId, Map<String, Object> optionals, Screen screen) {
        if (optionals.size() > 0) {
            Set<String> keys = optionals.keySet();
            for (String key : keys) {
                Object value = optionals.get(key);
                if (value != null) {
                    doStrategies(appId, deviceId, value, value.getClass(), screen);
                }
            }
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final void processFields(String appId, String deviceId, Object obj, Screen screen) {
        Class<?> clazz = obj.getClass();
        while (clazz != null && obj != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();

                try {
                    if (!Modifier.isFinal(field.getModifiers())) {
                        field.set(obj, doStrategies(appId, deviceId, field, obj, screen));
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    logger.error("Failed to set property value", e);
                }

                if (List.class.isAssignableFrom(type)) {
                    try {
                        List<Object> list = (List<Object>) field.get(obj);
                        if (list != null) {
                            for (int i = 0; i < list.size(); i++) {
                                Object fieldObj = list.get(i);
                                if (fieldObj != null) {
                                    list.set(i, doStrategies(appId, deviceId, fieldObj, fieldObj.getClass(), screen));
                                    if (shouldProcessFields(field, fieldObj.getClass())) {
                                        processFields(appId, deviceId, fieldObj, screen);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("", e);
                    }

                } else if (Collection.class.isAssignableFrom(type)) {
                    try {
                        Collection<?> collection = (Collection<?>) field.get(obj);
                        if (collection != null) {
                            Iterator<?> i = collection.iterator();
                            while (i.hasNext()) {
                                Object fieldObj = i.next();
                                if (fieldObj != null && shouldProcessFields(field, fieldObj.getClass())) {
                                    processFields(appId, deviceId, fieldObj, screen);
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("", e);
                    }
                } else if (Map.class.isAssignableFrom(type)) {
                    try {
                        Map<?, ?> map = (Map<?, ?>) field.get(obj);
                        if (map != null) {
                            for (Entry entry : map.entrySet()) {
                                Object entryValue = entry.getValue();
                                if (entryValue != null) {
                                    entry.setValue(doStrategies(appId, deviceId, entryValue, entryValue.getClass(), screen));
                                    if (shouldProcessFields(field, entryValue.getClass())) {
                                        processFields(appId, deviceId, entryValue, screen);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.warn("", e);
                    }
                } else if (shouldProcessFields(field, type)) {
                    try {
                        Object fieldObj = field.get(obj);
                        if (fieldObj != null) {
                            processFields(appId, deviceId, fieldObj, screen);
                        }
                    } catch (Exception e) {
                        logger.warn("", e);
                    }
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    private Object doStrategies(String appId, String deviceId, Field field, Object obj, Screen screen) {
        try {
            Object property = field.get(obj);
            Class<?> clazz = (property != null ? property.getClass() : field.getType());
            return doStrategies(appId, deviceId, property, clazz, screen);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            logger.error("Failed to crawl screen property", e);
        }
        return obj;
    }

    private Object doStrategies(String appId, String deviceId, Object property, Class<?> clazz, Screen screen) {

        for (IScreenPropertyStrategy s : screenProprtyStrategies) {
            property = s.doStrategy(appId, deviceId, property, clazz, screen);
        }

        return property;
    }

    private boolean shouldProcessFields(Field field, Class<?> clazz) {
        return clazz != null && !isWrapperType(clazz) && !Modifier.isStatic(field.getModifiers()) && !clazz.isPrimitive() && !clazz.isEnum()
                && !clazz.equals(Logger.class) && clazz.getPackage() != null && !clazz.getPackage().getName().startsWith("sun");
    }

}
