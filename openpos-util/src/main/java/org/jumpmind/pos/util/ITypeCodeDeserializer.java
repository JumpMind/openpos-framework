package org.jumpmind.pos.util;

import java.io.IOException;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.jumpmind.pos.util.ITypeCodeSerializer.ITypeCodeWrapper;
import org.jumpmind.pos.util.model.ITypeCode;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.jumpmind.pos.util.model.ITypeCodeRegistry;

@Slf4j
public class ITypeCodeDeserializer extends StdDeserializer<ITypeCode> {
    
    private static final long serialVersionUID = 1L;

    public ITypeCodeDeserializer() {
        super(ITypeCode.class);
    }

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
            try {
                Class typeCodeClass = Thread.currentThread().getContextClassLoader().loadClass(classesToTry[i]);
                returnTypeCode = ITypeCode.make(typeCodeClass, wrapper.value);
                break;
            } catch (ClassNotFoundException ex) {
                log.debug("ITypeCode class {} not found, will search for other locations if they are provided", classesToTry[i]);
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