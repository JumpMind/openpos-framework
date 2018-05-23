package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends Screen {

    private static final long serialVersionUID = 1L;

    List<MenuItem> menuItems = new ArrayList<>();

    public HomeScreen() {
        this.setType(ScreenType.Home);
    }

    public void setMenuItems(List<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public List<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void addMenuItem(MenuItem menuItem) {
        this.menuItems.add(menuItem);
    }

}
