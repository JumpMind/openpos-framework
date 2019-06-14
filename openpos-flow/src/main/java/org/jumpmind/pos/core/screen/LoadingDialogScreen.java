package org.jumpmind.pos.core.screen;

public class LoadingDialogScreen extends Screen {

	private static final long serialVersionUID = 1L;

	private String title;
	private String message;
	private boolean dismissable;
	private String dismissAction;
	
	public LoadingDialogScreen() {
		this.setScreenType(ScreenType.LoadingDialog);
		this.asDialog();
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

	public boolean isDismissable() {
		return dismissable;
	}

	public void setDismissable(boolean dismissable) {
		this.dismissable = dismissable;
	}

	public String getDismissAction() {
		return dismissAction;
	}

	public void setDismissAction(String dismissAction) {
		this.dismissAction = dismissAction;
	}
	
}
