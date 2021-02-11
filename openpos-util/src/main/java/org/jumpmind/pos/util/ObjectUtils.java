package org.jumpmind.pos.util;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class ObjectUtils {

    public static void mapFields(Object source, Object destination) {
        BeanUtils.copyProperties(source, destination);
    }

    public static void mapFields(Object source, Object destination, String ...ignoreProperties) {
        BeanUtils.copyProperties(source, destination, ignoreProperties);
    }

    public static <T> T deepClone(T object) {
        byte[] bytes = serialize(object);
        return deserialize(bytes);
    }

    @SuppressWarnings("unchecked")
    public static <T> T deserialize(byte[] bytes) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream in = new ObjectInputStream(bis);
            return (T) in.readObject();            
        } catch (Exception ex) {
            log.warn("Failed to deserialize byte array.", ex);
            return null;
        }
    }

    public static <T> byte[] serialize(T object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);   
            out.writeObject(object);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            log.warn("Failed to serialize object " + object, ex);
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                log.debug("Failed to close byte output stream object " + object, ex);
            }
        }      
        return null;
    }

    /**
     * Recursively searches the given object for all fields of the given type.
     * @param ofType The type of object to search for
     * @param parent The object to search recursively
     * @param <T> The type of object to search for
     * @return A list of objects found either directly on the given parent object or as a descendant.
     */
    public static <T> List<T> findRecursive(Class<T> ofType, Object parent) {
        ObjectFinder<T> finder = new ObjectFinder<>(ofType);
        finder.searchRecursive(parent);
        return finder.getResults();
    }


}
