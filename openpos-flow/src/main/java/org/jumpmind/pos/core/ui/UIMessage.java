package org.jumpmind.pos.core.ui;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.util.model.Message;


public class UIMessage extends Message {
    private static final long serialVersionUID = 1L;
    private String screenType;
    private String id;
    private int sessionTimeoutMillis;
    private Action sessionTimeoutAction;

    public UIMessage(String screenType, String id) {
        this();
        this.screenType = screenType;
        this.id = id;
    }

    public UIMessage() {
        setType(MessageType.Screen);
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getSessionTimeoutMillis() {
        return sessionTimeoutMillis;
    }

    public void setSessionTimeoutMillis(int sessionTimeoutMillis) {
        this.sessionTimeoutMillis = sessionTimeoutMillis;
    }

    public Action getSessionTimeoutAction() {
        return sessionTimeoutAction;
    }

    public void setSessionTimeoutAction(Action sessionTimeoutAction) {
        this.sessionTimeoutAction = sessionTimeoutAction;
    }

    public boolean isDialog() {
        String type = getType();
        return type != null && type.equals(MessageType.Dialog);
    }

    /**
     * Allows this screen content to be displayed in a Dialog on the client
     * side.
     */
    public UIMessage asDialog() {
        return this.asDialog(null);
    }

    /**
     * Allows this screen content to be displayed in a Dialog on the client
     * side.
     *
     * @param dialogProperties
     *            Additional properties that can control dialog behavior and
     *            rendering on the server side.
     */
    public UIMessage asDialog(DialogProperties dialogProperties) {
        this.setType(MessageType.Dialog);
        if (dialogProperties != null) {
            this.setDialogProperties(dialogProperties);
        }
        return this;
    }

    public void setDialogProperties(DialogProperties dialogProperties) {
        this.put("dialogProperties", dialogProperties);
    }

    public void addMessagePart(String messagePartName, Object messagePart) {
        this.put(messagePartName, messagePart);
    }
}
