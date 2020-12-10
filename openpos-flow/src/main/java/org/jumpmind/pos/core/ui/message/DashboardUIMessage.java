package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class DashboardUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private List<ActionItem> menuItems = new ArrayList<>();

    public DashboardUIMessage() {
        this.setScreenType(UIMessageType.DASHBOARD);
        this.setId("dashboard");
    }

    public List<ActionItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(List<ActionItem> menuItems) {
        this.menuItems = menuItems;
    }

    public void addMenuItem(ActionItem item) {
        if (this.menuItems == null) {
            this.menuItems = new ArrayList<>();
        }

        this.menuItems.add(item);
    }


}
