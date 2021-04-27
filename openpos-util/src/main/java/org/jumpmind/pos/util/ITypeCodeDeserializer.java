package org.jumpmind.pos.util;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.jumpmind.pos.util.ITypeCodeSerializer.ITypeCodeWrapper;
import org.jumpmind.pos.util.model.ITypeCode;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.jumpmind.pos.util.model.ITypeCodeRegistry;
import org.springframework.cache.support.NullValue;

@Slf4j
public class ITypeCodeDeserializer extends StdDeserializer<ITypeCode> {
    
    private static final long serialVersionUID = 1L;

    public ITypeCodeDeserializer() {
        super(ITypeCode.class);
    }

    private static java.util.Map<String, Class<?>> typeCodeClasses = new ConcurrentHashMap();

    @Override
    public ITypeCode deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ITypeCodeWrapper wrapper = jp.readValueAs(ITypeCodeWrapper.class);
        String[] classesToTry;
        boolean searchForCompatibleClass = false;

        if (wrapper.hasDeserializationAlternatives()) {
            classesToTry = ArrayUtils.addAll(wrapper.deserializationSearchClasses,
                ArrayUtils.contains(wrapper.deserializationSearchClasses, wrapper.clazz) ? null : wrapper.clazz
            );
        } else {
            classesToTry = new String[]{ wrapper.clazz };
            // In the event the given clazz cannot be found (because the type code has perhaps been moved to another package)
            // set this flag so that we can check the ITypeCodeRegistry for a potential compatible class
            searchForCompatibleClass = true;
        }

        ITypeCode returnTypeCode = null;
        for (int i = 0; i < classesToTry.length; i++) {
            if (! typeCodeClasses.containsKey(classesToTry[i])) {
                try {
                    Class typeCodeClass = Thread.currentThread().getContextClassLoader().loadClass(classesToTry[i]);
                    typeCodeClasses.put(classesToTry[i], typeCodeClass);
                } catch (ClassNotFoundException ex) {
                    typeCodeClasses.put(classesToTry[i], NullValue.INSTANCE.getClass());
                    log.debug("ITypeCode class {} not found, will search for other locations if they are provided", classesToTry[i]);
                }
            }

            Class<?> potentialTypeCodeClass = typeCodeClasses.get(classesToTry[i]);
            if (potentialTypeCodeClass != NullValue.INSTANCE.getClass()) {
                returnTypeCode = ITypeCode.make((Class)potentialTypeCodeClass, wrapper.value);
                break;
            }
        }

        if (returnTypeCode == null) {
            if (searchForCompatibleClass) {
                // Now look through the Registry to see if there is a class that has this class in its deserializationSearchClasses
                Class<? extends ITypeCode> typeCodeClass = ITypeCodeRegistry.getCompatibleDeserializationClass(wrapper.clazz);
                if (typeCodeClass != null) {
                    returnTypeCode = ITypeCode.make(typeCodeClass, wrapper.value);
                    if (returnTypeCode != null) {
                        log.debug("Deserialized ITypeCode of type {} and value '{}' to compatible available ITypeCode of type {}",
                            wrapper.clazz, wrapper.value, typeCodeClass.getName());
                    }
                }
            }
            if (returnTypeCode == null) {
                throw new IOException(
                        String.format("Failed to find an ITypeCode for the following classes: %s", String.join(", ", classesToTry))
                );
            }
        }

        return returnTypeCode;
    }
}