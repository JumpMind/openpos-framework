package org.jumpmind.pos.core.ui.data;

import org.jumpmind.pos.core.screen.SelectionListItem;
import org.jumpmind.pos.core.ui.UIDataMessage;

import java.util.HashMap;
import java.util.Map;

public class SelectionListUIDataMessage extends UIDataMessage {

    private Map<Integer , SelectionListItem> items;
    private Map<Integer, SelectionListItem> disabledItems;

    public SelectionListUIDataMessage() {
        setDataType(UIDataMessageType.SELECTION_LIST_DATA);
    }

    public Map<Integer, SelectionListItem> getItems() {
        if(items == null) {
            items = new HashMap<>();
        }
        return items;
    }

    public void setItems(Map<Integer, SelectionListItem> items) {
        this.items = items;
    }

    public Map<Integer, SelectionListItem> getDisabledItems() {
        if(disabledItems == null) {
            disabledItems = new HashMap<>();
            for(Integer key: getItems().keySet()) {
                if(!getItems().get(key).isEnabled()) {
                    disabledItems.put(key, getItems().get(key));
                }
            }
        }
        return disabledItems;
    }

    private void setDisabledItems(Map<Integer, SelectionListItem> disabledItems) {
        this.disabledItems = disabledItems;
    }
}
