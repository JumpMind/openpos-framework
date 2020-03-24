package org.jumpmind.pos.hazelcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.jumpmind.pos.util.event.AppEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class SerializationWrapper<T> {
    private static final Logger log = LoggerFactory.getLogger(SerializationWrapper.class);

    @JsonProperty
    String value;

    @JsonProperty("class")
    Class<? extends T> clazz;

    @SuppressWarnings("unused")
    SerializationWrapper() {}

    public <O extends T> SerializationWrapper(O toBeWrapped) {
        try {
            this.value = toBeWrapped != null ? DefaultObjectMapper.defaultObjectMapper().writeValueAsString(toBeWrapped) : null;
        } catch (JsonProcessingException e) {
            log.error(String.format("Failed to convert AppEvent of type '%s' to a String", toBeWrapped.getClass().getName()), e);
        }
        this.clazz = toBeWrapped != null ? (Class<? extends T>) toBeWrapped.getClass() : null;
    }

    public <O extends T> O deserialize(String s) throws IOException {
        SerializationWrapper<O> wrapper = null;
        try {
            wrapper = DefaultObjectMapper.defaultObjectMapper().readValue(s, SerializationWrapper.class);
        } catch (Exception ex) {
            log.error("Failed to deserialize AppEventWrapper!", ex);
            throw new IOException(ex);
        }

        O obj = null;
        if (wrapper.clazz != null) {
            if (wrapper.value != null) {
                try {
                    obj = DefaultObjectMapper.defaultObjectMapper().readValue(wrapper.value, wrapper.clazz);
                } catch (Exception ex) {
                    log.error(String.format("Failed to deserialize AppEvent of type '%s' from string [%s]",
                            wrapper.clazz.getName(), wrapper.value), ex);
                    throw new IOException(ex);
                }
            } else {
                throw new IOException(String.format("AppEvent of type '%s' is missing its data", wrapper.clazz.getName()));
            }
        } else {
            throw new IOException("AppEventWrapper class is null, cannot deserialize AppEvent");
        }

        return obj;
    }

}
