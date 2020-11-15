package org.jumpmind.pos.core.audio;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.message.UIMessageType;
import org.jumpmind.pos.util.model.Message;

@Data
@Builder
public class AudioConfigMessage extends Message {
    private static final long serialVersionUID = 1L;

    private AudioConfig config;

    @Override
    public String getType() {
        return UIMessageType.AUDIO_CONFIG;
    }
}
