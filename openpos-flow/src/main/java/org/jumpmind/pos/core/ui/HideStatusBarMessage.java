package org.jumpmind.pos.core.ui;

import lombok.Data;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

@Data
public class HideStatusBarMessage extends Message {
    public HideStatusBarMessage() {
        setType(MessageType.HideStatusBar);
    }
}