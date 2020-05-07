package org.jumpmind.pos.core.model;

import org.jumpmind.pos.util.model.Message;

public class StartupMessage extends Message {

    private boolean complete;

    private String displayMessage;

    public StartupMessage() {
        setType(MessageType.Startup);
    }

    public StartupMessage(boolean complete, String displayMessage) {
        this();
        this.complete = complete;
        this.displayMessage = displayMessage;
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

    public boolean isComplete() {
        return complete;
    }

    public void setComplete(boolean complete) {
        this.complete = complete;
    }
}
