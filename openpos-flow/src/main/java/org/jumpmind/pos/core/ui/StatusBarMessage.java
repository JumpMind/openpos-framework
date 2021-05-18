package org.jumpmind.pos.core.ui;

import lombok.Data;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

@Data
public class StatusBarMessage extends Message {
    public StatusBarMessage() {
        setType(MessageType.StatusBar);
    }
}