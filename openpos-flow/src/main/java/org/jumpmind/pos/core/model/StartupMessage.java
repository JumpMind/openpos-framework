package org.jumpmind.pos.core.model;

import org.jumpmind.pos.util.model.Message;

public class StartupMessage extends Message {

    private String displayMessage;

    public StartupMessage() {
        setType(MessageType.Startup);
    }

    public String getDisplayMessage() {
        return displayMessage;
    }

    public void setDisplayMessage(String displayMessage) {
        this.displayMessage = displayMessage;
    }

}
