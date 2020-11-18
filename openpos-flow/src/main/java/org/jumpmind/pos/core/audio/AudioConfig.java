package org.jumpmind.pos.core.audio;

import lombok.Data;
import org.jumpmind.pos.util.ObjectUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.Serializable;
import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "openpos.audio")
public class AudioConfig implements Serializable {
    private Boolean enabled;
    private Double volume;
    private List<AudioRequest> systemErrorSounds;
    private AudioInteractionSet interactions;

    @Override
    public Object clone() {
        AudioConfig copy = new AudioConfig();
        copy.setEnabled(this.getEnabled());
        copy.setVolume(this.getVolume());
        copy.setInteractions(ObjectUtils.deepClone(getInteractions()));
        return copy;
    }
}
