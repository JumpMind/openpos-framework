package org.jumpmind.pos.core.audio;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.message.UIMessageType;
import org.jumpmind.pos.util.model.Message;

import java.util.List;

@Data
@Builder
public class AudioPreloadMessage extends Message {
    private static final long serialVersionUID = 1L;
    private List<String> urls;

    @Override
    public String getType() {
        return UIMessageType.AUDIO_PRELOAD;
    }
}
