package org.jumpmind.pos.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.SerializerConfig;
import org.jumpmind.pos.core.device.DeviceStatus;
import org.jumpmind.pos.util.event.AppEvent;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class HazelcastTestConfig {

    @Bean
    public Config hazelCastConfig() {
        Config config = new Config().
                setProperty("hazelcast.logging.type", "slf4j");

        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setClass(AppEventStreamSerializer.class);
        serializerConfig.setTypeClass(AppEvent.class);
        config.getSerializationConfig().getSerializerConfigs().add(serializerConfig);

        serializerConfig = new SerializerConfig();
        serializerConfig.setClass(DeviceStatusStreamSerializer.class);
        serializerConfig.setTypeClass(DeviceStatus.class);

        config.getSerializationConfig().getSerializerConfigs().add(serializerConfig);
        return config;
    }

}
