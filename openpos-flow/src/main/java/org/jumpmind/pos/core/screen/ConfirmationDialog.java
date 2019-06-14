package org.jumpmind.pos.core.screen;

import java.io.Serializable;

public class ConfirmationDialog implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String title;
	private String message;
	private ActionItem confirmAction;
    private ActionItem cancelAction;
	
	public ConfirmationDialog() {
	    confirmAction = new ActionItem("Yes", "Yes");
	    confirmAction.setAutoAssignEnabled(false);
	    cancelAction = new ActionItem("No", "No");
	    cancelAction.setAutoAssignEnabled(false);
	}
	
	public ConfirmationDialog(String message) {
	    this();
	    this.message = message;
	}
	
	public ConfirmationDialog(String message, String title) {
	    this(message);
	    this.title = title;
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

	public String getConfirmButtonName() {
	    return this.confirmAction.getTitle();
	}

	public void setConfirmButtonName(String confirmButtonName) {
	    this.confirmAction.setTitle(confirmButtonName);
	}

	public ActionItem getConfirmAction() {
	    return this.confirmAction;
	}
	
	public void setConfirmAction(ActionItem confirmAction) {
	    this.confirmAction = confirmAction;
	}
	
	public String getCancelButtonName() {
	    return this.cancelAction.getTitle();
	}

	public void setCancelButtonName(String cancelButtonName) {
		this.cancelAction.setTitle(cancelButtonName);
	}
	
	public ActionItem getCancelAction() {
	    return this.cancelAction;
	}

    public void setCancelAction(ActionItem cancelAction) {
        this.cancelAction = cancelAction;
    }
	
}
