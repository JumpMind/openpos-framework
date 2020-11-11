package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.util.model.Message;

@Data
@Builder
public class AudioMessage extends Message {
    private static final long serialVersionUID = 1L;
    private String url;
    private Double playbackRate;
    private Double startTime;
    private Double endTime;
    private Boolean loop;
    private Double volume;
    private Boolean autoplay;
    private Double delayTime;

    @Override
    public String getType() {
        return UIMessageType.AUDIO;
    }
}
