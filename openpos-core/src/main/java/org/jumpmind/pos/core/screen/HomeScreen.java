package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends SellScreen {

    private static final long serialVersionUID = 1L;
    
    List<MenuItem> menuItems = new ArrayList<>();
    
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
