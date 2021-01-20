package org.jumpmind.pos.symds;

import lombok.Data;
import org.jumpmind.symmetric.io.data.DataEventType;

import java.util.Map;

@Data
public class SyncData {

    private String channelId;
    private String tableName;
    private DataEventType dataEventType;
    private Map<String, String> data;
    private Map<String, Object> context;

}
