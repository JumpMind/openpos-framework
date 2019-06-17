package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jumpmind.pos.core.model.MessageType;

public class DialogScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private List<ActionItem> buttons = new ArrayList<>();
    
    private String title;
    
    private String subType;
    
    private List<String> message = new ArrayList<>();
    
    private List<Line> messageLines = new ArrayList<>();    
    
    public DialogScreen() {
        setType(MessageType.Dialog);
        setScreenType(ScreenType.Dialog);
    }
    
    public DialogScreen(String title, ActionItem button) {
        this();
        this.title = title;
        this.addButton(button);
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
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }

    public DialogScreen addMessage(String message) {
        this.message.add(message);
        return this;
    }
    
    // @JsonIgnore
    public void setMessage(String...messages) {
        this.setMessage(Arrays.asList(messages));
    }
    
    public void setMessage(List<String> message) {
        this.message = message;
    }
    
    public List<String> getMessage() {
        return message;
    }
    
    public void setSubType(String subType) {
        this.subType = subType;
    }
    
    public String getSubType() {
        return subType;
    }

    public List<Line> getMessageLines() {
        return messageLines;
    }

    public void setMessageLines(List<Line> messageLines) {
        this.messageLines = messageLines;
    }
    
    public DialogScreen addMessageLine(Line line) {
        this.messageLines.add(line);
        return this;
    }
    
    public void setMessageLines(Line...lines) {
        this.setMessageLines(Arrays.asList(lines));
    }
    
}
