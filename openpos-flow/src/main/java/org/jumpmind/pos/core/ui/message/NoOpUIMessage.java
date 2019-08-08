package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class NoOpUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    public NoOpUIMessage() {
        this.setScreenType(UIMessageType.NO_OP);
    }

}