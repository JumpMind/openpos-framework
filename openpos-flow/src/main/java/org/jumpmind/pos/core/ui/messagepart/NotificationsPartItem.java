package org.jumpmind.pos.core.ui.messagepart;

import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotificationsPartItem extends ActionItem implements Serializable {
    private List<String> subItems;

    public NotificationsPartItem(String action, String title, String icon) {
        super(action, title, icon);
    }

    public List<String> getSubItems() {
        return subItems;
    }

    public void setSubItems(List<String> subItems) {
        this.subItems = subItems;
    }

    public NotificationsPartItem addSubItem( String item) {
        if( subItems == null ){
            subItems = new ArrayList<>();
        }

        subItems.add(item);

        return this;
    }
}