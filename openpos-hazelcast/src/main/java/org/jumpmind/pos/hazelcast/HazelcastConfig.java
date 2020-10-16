package org.jumpmind.pos.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.SerializerConfig;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.model.DeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;


@Configuration
@Profile("hazelcast")
public class HazelcastConfig {

    @Autowired
    HazelcastEnvConfig envConfig;
    

    @Bean
    @Primary
    public Config hazelCastConfig() {
        if (envConfig.getProperty("hazelcast.logging.type") == null) {
            envConfig.setProperty("hazelcast.logging.type", "slf4j");
        }
        
        if (StringUtils.isBlank(envConfig.getInstanceName())) {
            envConfig.setInstanceName("hazelcast-instance");
        }
        
        envConfig.addMapConfig(
            new MapConfig()
                .setName("configuration")
                .setTimeToLiveSeconds(-1)
        );
        

        SerializerConfig serializerConfig = new SerializerConfig();
        serializerConfig.setClass(AppEventStreamSerializer.class);
        serializerConfig.setTypeClass(AppEvent.class);
        envConfig.getSerializationConfig().getSerializerConfigs().add(serializerConfig);

        serializerConfig = new SerializerConfig();
        serializerConfig.setClass(DeviceStatusStreamSerializer.class);
        serializerConfig.setTypeClass(DeviceStatus.class);

        envConfig.getSerializationConfig().getSerializerConfigs().add(serializerConfig);
        return envConfig;
    }

}
