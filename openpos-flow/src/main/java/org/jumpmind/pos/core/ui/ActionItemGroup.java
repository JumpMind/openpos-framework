package org.jumpmind.pos.core.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ActionItemGroup implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String title;
    private String keybind;
    private List<ActionItem> actionItems= new ArrayList<>();
    
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeybind() {
        return keybind;
    }
    public void setKeybind(String keybind) {
        this.keybind = keybind;
    }
    public List<ActionItem> getActionItems() {
        return actionItems;
    }
    public void setActionItems(List<ActionItem> actionItems) {
        this.actionItems = actionItems;
    }
    
    

}
