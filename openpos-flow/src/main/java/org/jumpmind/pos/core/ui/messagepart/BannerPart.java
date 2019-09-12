package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.screen.ActionItem;

public class BannerPart implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        INFO, WARNING, ERROR
    }
    
    private String iconName;
    private MessageType messageType;
    private List<String> messages;
    private List<ActionItem> buttons;
    
    public BannerPart() {
        
    }
    
    public BannerPart(String iconName, MessageType messageType, List<String> messages, List<ActionItem> buttons) {
        super();
        this.iconName = iconName;
        this.messageType = messageType;
        this.messages = messages;
        this.buttons = buttons;
    }
    
    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }
    
    public BannerPart addMessage(String message) {
        if (messages == null) {
            messages = new ArrayList<>();
        }
        messages.add(message);
        return this;
    }

    public List<ActionItem> getButtons() {
        return buttons;
    }
    
    public void setButtons(List<ActionItem> buttons) {
        this.buttons = buttons;
    }
    
    public BannerPart addButton(ActionItem button) {
        if (buttons == null) {
            buttons = new ArrayList<>();
        }
        buttons.add(button);
        return this;
    }
}
