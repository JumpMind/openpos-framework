package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.data.Line;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.DialogHeaderPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private List<ActionItem> buttons = new ArrayList<>();

    private List<String> message = new ArrayList<>();

    private List<Line> messageLines = new ArrayList<>();

    public DialogUIMessage() {
        setScreenType(UIMessageType.DIALOG);
        this.asDialog();
    }

    public DialogUIMessage(String title, ActionItem button) {
        this();
        DialogHeaderPart dialogHeader = new DialogHeaderPart();
        dialogHeader.setHeaderText(title);
        addMessagePart(MessagePartConstants.DialogHeader, dialogHeader);
        this.addButton(button);
    }

    // Intentionally omitted getter for title so Jackson won't add title
    // attribute to serialized JSON
    public void setTitle(String title) {
        DialogHeaderPart dialogHeader = new DialogHeaderPart(title);
        addMessagePart(MessagePartConstants.DialogHeader, dialogHeader);
    }

    public List<ActionItem> getButtons() {
        return buttons;
    }

    public void setButtons(List<ActionItem> buttons) {
        this.buttons = buttons;
    }

    public void addButton(ActionItem button) {
        this.buttons.add(button);
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(String... messages) {
        this.setMessage(Arrays.asList(messages));
    }
    
    public void setMessage(List<String> message) {
        this.message = message;
    }

    public DialogUIMessage addMessage(String message) {
        this.message.add(message);
        return this;
    }

    public List<Line> getMessageLines() {
        return messageLines;
    }

    public void setMessageLines(List<Line> messageLines) {
        this.messageLines = messageLines;
    }

    public DialogUIMessage addMessageLine(Line line) {
        this.messageLines.add(line);
        return this;
    }
}
