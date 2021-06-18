package org.jumpmind.pos.core.model;

import org.jumpmind.pos.util.model.Message;

public class DataClearMessage extends Message {
    public DataClearMessage() {
        setType(MessageType.DataClear);
    }
}
