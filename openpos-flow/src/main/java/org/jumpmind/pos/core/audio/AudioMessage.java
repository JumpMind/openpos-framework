package org.jumpmind.pos.core.audio;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.message.UIMessageType;
import org.jumpmind.pos.util.model.Message;

@Data
@Builder
public class AudioMessage extends Message {
    private static final long serialVersionUID = 1L;
    private String sound;
    private Double playbackRate;
    private Double startTime;
    private Double endTime;
    private Boolean loop;
    private Double volume;
    private Boolean autoplay;
    private Double delayTime;
    private Boolean reverse;

    @Override
    public String getType() {
        return UIMessageType.AUDIO;
    }
}
