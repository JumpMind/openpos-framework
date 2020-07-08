package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class LoadingDialogUIMessage extends UIMessage {

    private String message;

    private ActionItem cancelButton;

    public LoadingDialogUIMessage() {
        this.setScreenType(UIMessageType.LOADING_DIALOG);
        this.asDialog();
    }

    @Builder
    public LoadingDialogUIMessage(String message, ActionItem cancelButton) {
        this();
        this.message = message;
        this.cancelButton = cancelButton;
    }

}
