package org.jumpmind.pos.core.audio;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;

@Data
@Configuration
@ConfigurationProperties(prefix = "openpos.audio")
public class AudioConfig implements Serializable {
    private Boolean enabled;
    private AudioInteractionSet interactions;
}
