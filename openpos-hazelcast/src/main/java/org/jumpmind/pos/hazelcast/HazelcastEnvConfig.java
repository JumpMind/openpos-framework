package org.jumpmind.pos.hazelcast;

import com.hazelcast.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;


@ConfigurationProperties(prefix = "hazelcast", ignoreUnknownFields = false)
@Profile("hazelcast")
@Component
public class HazelcastEnvConfig extends Config {

}
