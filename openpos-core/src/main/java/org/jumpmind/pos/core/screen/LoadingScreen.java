package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents status screen/dialog.
 */
public class LoadingScreen extends Screen {

    private static final long serialVersionUID = 1L;

    private String title;
    private List<String> message = new ArrayList<>();

    public LoadingScreen() {
        setType(ScreenType.Loading);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        if (message == null) {
            this.message.clear();
        } else {
            this.message = message;
        }
    }

    public void setMessage(String message) {
        this.message.clear();
        if (message != null) {
            this.message.add(message);
        }
    }
    
}
