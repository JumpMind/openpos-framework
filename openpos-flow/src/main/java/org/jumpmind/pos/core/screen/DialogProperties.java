package org.jumpmind.pos.core.screen;

import java.io.Serializable;
/**
 * Refer to MatDialogConfig properties defined <a href="https://material.angular.io/components/dialog/api#MatDialogConfig">here</a>  
 * for list of properties that can be supported as needed.  Also includes OpenPOS specific properties
 */
public class DialogProperties implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Use this to force a new dialog to be create if dialogs are being shown back to back.  The default behavior
     * is to reuse the previous dialog and just swap out the content.
     */
    private String minWidth;
    private String minHeight;
    private boolean executeActionBeforeClose = false;
    private boolean closeable = false;
    private boolean autoFocus = true;
    private boolean restoreFocus = true;

    public DialogProperties() {
    }
    
    public DialogProperties(boolean closeable) {
        this.closeable = closeable;
    }
    
    public boolean isExecuteActionBeforeClose() {
        return executeActionBeforeClose;
    }

    public void setExecuteActionBeforeClose(boolean executeActionBeforeClose) {
        this.executeActionBeforeClose = executeActionBeforeClose;
    }
    
    public DialogProperties executeActionBeforeClose(boolean execBeforeClose) {
        this.setExecuteActionBeforeClose(execBeforeClose);
        return this;
    }
    
    public boolean isCloseable() {
        return closeable;
    }

	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
	}

    public DialogProperties closeable(boolean closeable) {
        this.setCloseable(closeable);
        return this;
    }
	
	public void setAutoFocus(boolean autoFocus) {
        this.autoFocus = autoFocus;
    }
	
    public boolean isAutoFocus() {
        return autoFocus;
    }
    
    public DialogProperties autoFocus(boolean autoFocus) {
        this.setAutoFocus(autoFocus);
        return this;
    }

    public String getMinWidth() {
        return minWidth;
    }

    public void setMinWidth(String minWidth) {
        this.minWidth = minWidth;
    }

    public DialogProperties minWidth(String minWidth) {
        this.setMinWidth(minWidth);
        return this;
    }
    
    public String getMinHeight() {
        return minHeight;
    }

    public void setMinHeight(String minHeight) {
        this.minHeight = minHeight;
    }

    public DialogProperties minHeight(String minHeight) {
        this.setMinHeight(minHeight);
        return this;
    }
    
    public void setRestoreFocus(boolean restoreFocus) {
        this.restoreFocus = restoreFocus;
    }
    
    public boolean isRestoreFocus() {
        return restoreFocus;
    }
    
}
