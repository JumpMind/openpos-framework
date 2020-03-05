package org.jumpmind.pos.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.SerializerConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.spring.cache.HazelcastCacheManager;
import org.jumpmind.pos.core.device.DeviceStatus;
import org.jumpmind.pos.util.event.AppEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.ConcurrentMap;


@Configuration
@Profile("hazelcast")
public class HazelcastConfig {


    @Bean
    public Config hazelCastConfig() {
        Config config = new Config().
                setProperty("hazelcast.logging.type", "slf4j").
                setInstanceName("hazelcast-instance")
                .addMapConfig(
                        new MapConfig()
                                .setName("configuration")
                                .setTimeToLiveSeconds(-1));

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
