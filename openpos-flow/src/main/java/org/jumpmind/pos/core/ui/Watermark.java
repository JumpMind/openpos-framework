package org.jumpmind.pos.core.ui;

import lombok.Getter;
import lombok.Setter;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

@Getter
@Setter
public class Watermark extends Message {

    String screenMessage;

    public Watermark() {
        setType(MessageType.Watermark);
    }

    public Watermark(String message) {
        this();
        this.screenMessage = message;
    }
}