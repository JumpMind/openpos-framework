package org.jumpmind.pos.core.ui.messagepart;

import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotificationsPartItem extends ActionItem implements Serializable {
    private Object actionData;
    private List<String> subItems;

    public NotificationsPartItem(String action, String title, String icon) {
        super(action, title, icon);
    }

    public NotificationsPartItem(String action, Object actionData, String title, String icon) {
        super(action, title, icon);
        this.actionData = actionData;
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

    public Object getActionData() {
        return actionData;
    }

    public void setActionData(Object actionData) {
        this.actionData = actionData;
    }
}