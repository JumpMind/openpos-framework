package org.jumpmind.pos;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan(
    basePackages = { "org.jumpmind.pos" }
)
@EnableConfigurationProperties
public class UtilTestConfig {

}
