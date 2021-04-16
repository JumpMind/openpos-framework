package org.jumpmind.pos.core.ui;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

public class HideWatermark extends Message {

    public HideWatermark() {
        setType(MessageType.HideWatermark);
    }
}