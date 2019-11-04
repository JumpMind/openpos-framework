package org.jumpmind.pos.server.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnProperty(
    value = "openpos.general.enableScheduling",
    havingValue = "true",
    matchIfMissing = true
)
@EnableScheduling
public class SchedulerConfig {

}
