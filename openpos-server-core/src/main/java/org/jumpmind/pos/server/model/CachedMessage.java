package org.jumpmind.pos.server.model;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.util.model.Message;

import java.util.Date;

@Data
@Builder
public class CachedMessage {
    private Message message;
    private Date cachedTime;
}
