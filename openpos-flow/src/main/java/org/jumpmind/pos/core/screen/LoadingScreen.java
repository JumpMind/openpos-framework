package org.jumpmind.pos.core.screen;

import org.jumpmind.pos.core.model.MessageType;

/**
 * Represents status screen/dialog.
 */
public class LoadingScreen extends Screen {

    private static final long serialVersionUID = 1L;

    private String title;
    private String message;

    public LoadingScreen() {
        setType(MessageType.Loading);
        setScreenType(ScreenType.Loading);
    }
    
    public LoadingScreen(String title) {
        this();
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
    
}
