package org.jumpmind.pos.core.screen;

import org.jumpmind.pos.core.model.MessageType;

public class ConfirmDialogScreen extends Screen {
	private static final long serialVersionUID = 1L;
	
    public ConfirmationDialog confirmationDialog;

	
    public ConfirmDialogScreen(ConfirmationDialog confirmationDialog) {
        setType(MessageType.Dialog);
        setScreenType(ScreenType.ConfirmDialog);
	    this.confirmationDialog = confirmationDialog;
	}

    public ConfirmationDialog getConfirmationDialog() {
        return confirmationDialog;
    }


    public void setConfirmationDialog(ConfirmationDialog confirmationDialog) {
        this.confirmationDialog = confirmationDialog;
    }
    
}
