package org.jumpmind.pos.core.audio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MouseAudioInteraction implements Serializable {
    private Boolean enabled;
    private AudioRequest mouseDown;
    private AudioRequest mouseUp;
}
