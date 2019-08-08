package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.core.ui.ConfirmationDialog;
import org.jumpmind.pos.core.ui.UIMessage;

public class ConfirmDialogUIMessage extends UIMessage {
	private static final long serialVersionUID = 1L;
	
    public ConfirmationDialog confirmationDialog;

	
    public ConfirmDialogUIMessage(ConfirmationDialog confirmationDialog) {
        setType(MessageType.Dialog);
        setScreenType(UIMessageType.CONFIRM_DIALOG);
	    this.confirmationDialog = confirmationDialog;
	}

    public ConfirmationDialog getConfirmationDialog() {
        return confirmationDialog;
    }


    public void setConfirmationDialog(ConfirmationDialog confirmationDialog) {
        this.confirmationDialog = confirmationDialog;
    }
    
}
