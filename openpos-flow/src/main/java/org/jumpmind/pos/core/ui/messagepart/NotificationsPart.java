package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotificationsPart implements Serializable {
    private String title;
    private String icon;
    private List<NotificationsPartItem> items;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<NotificationsPartItem> getItems() {
        return items;
    }

    public void setItems(List<NotificationsPartItem> items) {
        this.items = items;
    }

    public void addItem(NotificationsPartItem item) {
        if( items == null) {
            items = new ArrayList<>();
        }

        items.add(item);
    }
}