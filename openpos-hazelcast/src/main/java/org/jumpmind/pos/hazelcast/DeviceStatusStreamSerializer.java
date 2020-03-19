package org.jumpmind.pos.hazelcast;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.StreamSerializer;
import org.jumpmind.pos.core.device.DeviceStatus;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class DeviceStatusStreamSerializer implements StreamSerializer<DeviceStatus> {
    private static final Logger log = LoggerFactory.getLogger(DeviceStatusStreamSerializer.class);

    @Override
    public void write(ObjectDataOutput out, DeviceStatus deviceStatus) throws IOException {
        String deviceStatusStr = DefaultObjectMapper.defaultObjectMapper().writeValueAsString(deviceStatus);
        out.writeUTF(deviceStatusStr);
    }

    @Override
    public DeviceStatus read(ObjectDataInput in) throws IOException {
        DeviceStatus status = DefaultObjectMapper.defaultObjectMapper().readValue(in.readUTF(), DeviceStatus.class);
        return status;
    }

    @Override
    public int getTypeId() {
        return 2;
    }

    @Override
    public void destroy() {
    }

}
