package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends Screen {

    private static final long serialVersionUID = 1L;

    List<ActionItem> menuItems = new ArrayList<>();

    public HomeScreen() {
        this.setScreenType(ScreenType.Home);
    }

    public void setMenuItems(List<ActionItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<ActionItem> getMenuItems() {
        return menuItems;
    }

    public void addMenuItem(ActionItem menuItem) {
        this.menuItems.add(menuItem);
    }

}
