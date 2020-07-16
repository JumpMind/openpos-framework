package org.jumpmind.pos.util;

import lombok.extern.slf4j.Slf4j;
import org.joda.money.Money;
import org.slf4j.Logger;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Searches a given parent object recursively for all descendant Objects of a given type.  Does not search for
 * "simple types" such as primitives, Money, BigDecimal, String, Date.
 * @param <T> The type of the object to search for. Example usage:
 * <pre>
 *     ObjectFinder&lt;MyClass> finder = new ObjectFinder&lt;>(MyClass.class);<br/>
 *     finder.searchRecursive(parentObject);<br/>
 *     ArrayList&lt;MyClass> results = finder.getResults();<br/>
 *
 * </pre>
 *
 */
@Slf4j
public class ObjectFinder<T extends Object> {

    private Collection<T> results;
    private Class<T> targetType;
    private boolean distinctResults = true;
    private static final Set<Class<?>> WRAPPER_TYPES = new HashSet<>();
    static {
        WRAPPER_TYPES.add(Boolean.class);
        WRAPPER_TYPES.add(Character.class);
        WRAPPER_TYPES.add(Byte.class);
        WRAPPER_TYPES.add(Short.class);
        WRAPPER_TYPES.add(Integer.class);
        WRAPPER_TYPES.add(Long.class);
        WRAPPER_TYPES.add(Float.class);
        WRAPPER_TYPES.add(Double.class);
        WRAPPER_TYPES.add(Void.class);
    }

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    public ObjectFinder(Class<T> targetClass) {
        this(targetClass, true);
    }

    public ObjectFinder(Class<T> targetClass, boolean distinctResults) {
        targetType = targetClass;
        this.distinctResults = distinctResults;
        initResults();
    }

    public List<T> getResults() {
        return new ArrayList<>(results);
    }

    public void searchRecursive(Object obj) {
        if (obj == null) {
            return;
        }

        Class<?> clazz = obj.getClass();
        if (isSimpleType(clazz)) {
            return;
        }

        while (clazz != null) {
            Field[] fields = clazz.getDeclaredFields();
            for (Field field : fields) {
                field.setAccessible(true);
                Class<?> type = field.getType();
                try {
                    Object value = field.get(obj);

                    if (value != null) {
                        if (targetType.isAssignableFrom(type)) {
                            addToResults((T) value);
                        }

                        if (!searchCollections(value) && shouldSearch(field) && shouldSearch(type)) {
                            searchRecursive(value);
                        }
                    }
                } catch (Exception e) {
                    log.warn("", e);
                }
            }
            clazz = clazz.getSuperclass();
        }
    }

    public boolean isDistinctResults() {
        return distinctResults;
    }

    public void setDistinctResults(boolean distinctResults) {
        if (distinctResults != this.distinctResults) {
            this.distinctResults = distinctResults;
            initResults();
        }
    }

    protected void initResults() {
        this.results = distinctResults ? new HashSet<>() : new ArrayList<>();
    }

    protected void addToResults(T value) {
        this.results.add(value);
    }

    protected boolean shouldSearch(Field field) {
        return !Modifier.isStatic(field.getModifiers());
    }

    protected boolean shouldSearch(Class<?> clazz) {
        return clazz != null && !isWrapperType(clazz) && !clazz.isPrimitive() && !clazz.isEnum() && !clazz.equals(Logger.class)
                && clazz.getPackage() != null && !clazz.getPackage().getName().startsWith("sun");
    }

    protected boolean searchCollections(Object value) {
        if (value instanceof List<?>) {
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) value;
            for (int i = 0; i < list.size(); i++) {
                Object fieldObj = list.get(i);
                if (fieldObj != null) {
                    if (targetType.isAssignableFrom(fieldObj.getClass())) {
                        addToResults((T) fieldObj);
                    }

                    if (! searchCollections(fieldObj) && shouldSearch(fieldObj.getClass())) {
                        searchRecursive(fieldObj);
                    }
                }
            }
            return true;

        } else if (value instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) value;
            Iterator<?> i = collection.iterator();
            while (i.hasNext()) {
                Object fieldObj = i.next();
                if (fieldObj != null) {
                    if (targetType.isAssignableFrom(fieldObj.getClass())) {
                        addToResults((T) fieldObj);
                    }
                    if ( !searchCollections(fieldObj) && shouldSearch(fieldObj.getClass())) {
                        searchRecursive(fieldObj);
                    }
                }
            }
            return true;
        } else if (value != null && value.getClass().isArray() && ! value.getClass().isPrimitive() && targetType.isAssignableFrom(value.getClass().getComponentType())) {
            // Only process arrays that hold objects of the given target type
            for (int i = 0; i < Array.getLength(value); i++) {
                Object arrayElem = Array.get(value, i);
                if (arrayElem != null) {
                    if (targetType.isAssignableFrom(arrayElem.getClass())) {
                        addToResults((T) arrayElem);
                    }
                }
            }
            return true;
        } else if (value instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<Object, Object> map = (Map<Object, Object>) value;
            for (Map.Entry<Object, Object> entry : map.entrySet()) {
                Object entryValue = entry.getValue();
                if (entryValue != null) {
                    if (targetType.isAssignableFrom(entryValue.getClass())) {
                        addToResults((T) entryValue);
                    }

                    if (! searchCollections(entryValue) && shouldSearch(entryValue.getClass())) {
                        searchRecursive(entryValue);
                    }
                }
            }
            return true;

        } else {
            return false;
        }
    }

    protected boolean isSimpleType(Class<?> clazz) {
        if (clazz.isPrimitive()
                || String.class == clazz
                || BigDecimal.class == clazz
                || Money.class == clazz
                || Date.class == clazz) {
            return true;
        } else {
            return false;
        }
    }

}
