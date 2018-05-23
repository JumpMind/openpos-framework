package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DialogScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private List<MenuItem> buttons = new ArrayList<>();
    
    private String title;
    
    private String subType;
    
    private List<String> message = new ArrayList<>();
    
    private DialogProperties dialogProperties;
    
    public DialogScreen() {
        setType(ScreenType.Dialog);
        getTemplate().setDialog(true);
    }
    
    public List<MenuItem> getButtons() {
        return buttons;
    }

    public void setButtons(List<MenuItem> buttons) {
        this.buttons = buttons;
    }
    
    public void addButton(MenuItem button) {
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
    
    public void setMessage(String...messages) {
        this.setMessage(Arrays.asList(messages));
    }
    
    public void setMessage(List<String> message) {
        this.message = message;
    }
    
    public List<String> getMessage() {
        return message;
    }

    protected DialogProperties getDialogProperties() {
        return dialogProperties;
    }

    protected void setDialogProperties(DialogProperties dialogProperties) {
        this.dialogProperties = dialogProperties;
    }
    
    public void setSubType(String subType) {
        this.subType = subType;
    }
    
    public String getSubType() {
        return subType;
    }
    
}
