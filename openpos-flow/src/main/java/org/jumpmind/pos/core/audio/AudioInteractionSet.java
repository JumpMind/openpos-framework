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
public class AudioInteractionSet implements Serializable {
    private Boolean enabled;
    private MouseAudioInteraction mouse;
    private TouchAudioInteraction touch;
    private DialogAudioInteraction dialog;
}
