package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@AssignKeyBindings
public class HomeUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    private List<ActionItem> menuItems = new ArrayList<>();

    public HomeUIMessage(){
        setId("home");
        setScreenType(UIMessageType.HOME);
    }

    public List<ActionItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<ActionItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addMenuItem(ActionItem item) {
        if( this.menuItems == null ) {
            this.menuItems = new ArrayList<>();
        }

        this.menuItems.add(item);
    }
}
