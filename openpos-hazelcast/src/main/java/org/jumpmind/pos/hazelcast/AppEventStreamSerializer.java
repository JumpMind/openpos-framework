package org.jumpmind.pos.hazelcast;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.jumpmind.pos.util.event.AppEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class AppEventStreamSerializer implements StreamSerializer<AppEvent> {
    private static final Logger log = LoggerFactory.getLogger(AppEventStreamSerializer.class);

    @Override
    public void write(ObjectDataOutput out, AppEvent appEvent) throws IOException {
        // Use a wrapper for serializing so that subclass instances of AppEvent are properly re-constituted upon
        // deserialization
        SerializationWrapper<AppEvent> wrapper = new SerializationWrapper<>(appEvent);
        String appEventWrapperStr = DefaultObjectMapper.defaultObjectMapper().writeValueAsString(wrapper);
        out.writeUTF(appEventWrapperStr);
    }

    @Override
    public AppEvent read(ObjectDataInput in) throws IOException {
        SerializationWrapper<AppEvent> wrapper = new SerializationWrapper<>();
        AppEvent appEvent = wrapper.deserialize(in.readUTF());
        return appEvent;
    }

    @Override
    public int getTypeId() {
        return 1;
    }

    @Override
    public void destroy() {
    }

}
