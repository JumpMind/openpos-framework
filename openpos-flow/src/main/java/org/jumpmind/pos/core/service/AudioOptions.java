package org.jumpmind.pos.core.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AudioOptions {
    private Double playbackRate;
    private Double startTime;
    private Double endTime;
    private Boolean loop;
    private Double volume;
    private Boolean autoplay;
    private Double delayTime;
}
