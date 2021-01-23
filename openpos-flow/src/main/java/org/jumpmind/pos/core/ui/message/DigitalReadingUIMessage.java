package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class DigitalReadingUIMessage extends UIMessage {

    public DigitalReadingUIMessage() {
        setScreenType("DigitalReading");
    }

    String message;
    String icon;
    String value;
    String instructions;
    ActionItem button;
}
