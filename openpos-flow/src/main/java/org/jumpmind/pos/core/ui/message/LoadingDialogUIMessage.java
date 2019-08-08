package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class LoadingDialogUIMessage extends UIMessage {

    private String message;

    public LoadingDialogUIMessage() {
        this.setScreenType(UIMessageType.LOADING_DIALOG);
        this.asDialog();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
