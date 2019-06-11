package org.jumpmind.pos.core.ui.data;

import org.jumpmind.pos.core.screen.SelectionListItem;
import org.jumpmind.pos.core.screen.SelectionMode;
import org.jumpmind.pos.core.ui.UIDataMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SelectionListUIDataMessage extends UIDataMessage {

    private List<SelectionListItem> items;
    private List<SelectionListItem> disabledItems;
    private int numberItemsPerPage;
    private int numberTotalItems;
    private int defaultSelectItemIndex = 0;
    private SelectionMode selectionMode;

    public SelectionListUIDataMessage() {
        setDataType(UIDataMessageType.SELECTION_LIST_DATA);
    }

    public List<SelectionListItem> getItems() {
        if(items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<SelectionListItem> items) {
        this.items = items;
    }

    public List<SelectionListItem> getDisabledItems() {
        if(disabledItems == null) {
            setDisabledItems(items.stream().filter(i->!i.isEnabled()).collect(Collectors.toList()));
        }
        return disabledItems;
    }

    private void setDisabledItems(List<SelectionListItem> disabledItems) {
        this.disabledItems = disabledItems;
    }

    public int getNumberItemsPerPage() {
        return numberItemsPerPage;
    }

    public void setNumberItemsPerPage(int numberItemsPerPage) {
        this.numberItemsPerPage = numberItemsPerPage;
    }

    public int getNumberTotalItems() {
        return numberTotalItems;
    }

    public void setNumberTotalItems(int numberTotalItems) {
        this.numberTotalItems = numberTotalItems;
    }

    public int getDefaultSelectItemIndex() {
        return defaultSelectItemIndex;
    }

    public void setDefaultSelectItemIndex(int defaultSelectItemIndex) {
        this.defaultSelectItemIndex = defaultSelectItemIndex;
    }

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }
}
