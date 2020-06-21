package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

public class LoadingDialogUIMessage extends UIMessage {

    private String message;

    private ActionItem cancelButton;

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

    public ActionItem getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(ActionItem cancelButton) {
        this.cancelButton = cancelButton;
    }
}
