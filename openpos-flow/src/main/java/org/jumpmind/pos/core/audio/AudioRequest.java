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
public class AudioRequest implements Serializable {
    private String sound;
    private Double playbackRate;
    private Double startTime;
    private Double endTime;
    private Boolean loop;
    private Double volume;
    private Boolean autoplay;
    private Double delayTime;
    private String group;

    public Object clone() {
        return AudioRequest.builder()
                .sound(sound)
                .playbackRate(playbackRate)
                .startTime(startTime)
                .endTime(endTime)
                .loop(loop)
                .volume(volume)
                .autoplay(autoplay)
                .delayTime(delayTime)
                .group(group)
                .build();
    }
}
