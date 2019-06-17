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
    private List<ActionItem> itemActions = new ArrayList<>();
    private boolean condensedListDisplay = false;
    private String action = "Next";
    private ActionItem actionButton = null;
    private String customerName;
    private String noCustomerText;

    public ItemListScreen() {
        setScreenType(ScreenType.ItemList);
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

    public List<ActionItem> getItemActions() {
        return itemActions;
    }


    public void setItemActions(List<ActionItem> itemActions) {
        this.itemActions = itemActions;
    }

    public void addItemAction(ActionItem itemAction) {
        this.getItemActions().add(itemAction);
    }
    
    public String getAction() {
        return action;
    }
    
    public void setAction(String action) {
        this.action = action;
    }

    public ActionItem getActionButton() {
        return actionButton;
    }

    public void setActionButton(ActionItem actionButton) {
        this.actionButton = actionButton;
    }
    
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerName() {
        return customerName;
    }
    
    public void setNoCustomerText(String noCustomerText) {
        this.noCustomerText = noCustomerText;
    }
    
    public String getNoCustomerText() {
        return noCustomerText;
    }

    
}
