package org.jumpmind.pos.util;

import java.io.ByteArrayInputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import org.apache.log4j.Logger;

public class ObjectUtils {

    private static Logger log = Logger.getLogger(ObjectUtils.class);

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
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);   
            out.writeObject(object);
            out.flush();
            byte[] bytes = bos.toByteArray();
            return bytes;
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

}
