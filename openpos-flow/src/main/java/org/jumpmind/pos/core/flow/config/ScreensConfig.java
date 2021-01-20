package org.jumpmind.pos.core.flow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "openpos.screens")
@Getter
@Setter
public class ScreensConfig {

    Map<String, ScreenConfig> config;

}
