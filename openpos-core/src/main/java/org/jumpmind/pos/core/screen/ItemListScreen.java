package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;


public class ItemListScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private List<IItem> items = new ArrayList<>();
    private SelectionMode selectionMode = SelectionMode.None;
    private int selectedIndex = -1;
    private List<Integer> selectedIndexes = new ArrayList<>();
    private String itemActionName = "Item";
    private String text;
    private List<MenuItem> itemActions = new ArrayList<>();
    private boolean condensedListDisplay = false;
    private String action = "Next";
    private MenuItem actionButton = null;

    public ItemListScreen() {
        setType(ScreenType.ItemList);
    }
    
    
    public List<IItem> getItems() {
        return items;
    }
    
    public void setItems(List<IItem> items) {
        this.items = items;
    }
    
    public void addItem(IItem item) {
        this.getItems().add(item);
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
 
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }
    
    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setSelectedIndex(int selectedIndex) {
       this.selectedIndex = selectedIndex;
    }
    
    public int getSelectedIndex() {
        return this.selectedIndex;
    }
    
    public List<Integer> getSelectedIndexes() {
        return this.selectedIndexes;
    }

    public void setSelectedIndexes(List<Integer> selectedIndexes) {
        this.selectedIndexes = selectedIndexes;
    }


    public boolean isCondensedListDisplay() {
        return condensedListDisplay;
    }


    public void setCondensedListDisplay(boolean condensedListDisplay) {
        this.condensedListDisplay = condensedListDisplay;
    }


    public String getItemActionName() {
        return itemActionName;
    }


    public void setItemActionName(String itemActionName) {
        this.itemActionName = itemActionName;
    }

    public List<MenuItem> getItemActions() {
        return itemActions;
    }


    public void setItemActions(List<MenuItem> itemActions) {
        this.itemActions = itemActions;
    }

    public void addItemAction(MenuItem itemAction) {
        this.getItemActions().add(itemAction);
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }

    public MenuItem getActionButton() {
        return actionButton;
    }

    public void setActionButton(MenuItem actionButton) {
        this.actionButton = actionButton;
    }

    
}
