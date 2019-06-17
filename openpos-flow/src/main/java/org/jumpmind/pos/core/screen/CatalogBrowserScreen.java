package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CatalogBrowserScreen extends DynamicFormScreen {
    private static final long serialVersionUID = 1L;

    public static final int DEFAULT_MAX_ITEMS_PER_PAGE = 25;
    
    private List<SellItem> items =  new ArrayList<>();
    private Integer maxItemsPerPage = DEFAULT_MAX_ITEMS_PER_PAGE;
    private List<ActionItem> categories = new ArrayList<>();
    private Integer selectedItemQuantity;
    private Integer itemStartIndex = 0;
    private Integer itemEndIndex = DEFAULT_MAX_ITEMS_PER_PAGE-1;
    private Integer itemTotalCount;
    
    public CatalogBrowserScreen() {
        setScreenType(ScreenType.CatalogBrowser);
    }

    public void addItems(SellItem... items) {
        this.getItems().addAll(Arrays.asList(items));
    }
    
    public void addItem(SellItem item) {
        this.getItems().add(item);
    }
    
    public List<SellItem> getItems() {
        return items;
    }
    
    public void setItems(List<SellItem> items) {
        this.items = items;
    }
    
    public CatalogBrowserScreen items(List<SellItem> items) {
        this.setItems(items);
        return this;
    }
    
    public Integer getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
    
    public void setMaxItemsPerPage(Integer maxItemsPerPage) {
        this.maxItemsPerPage = maxItemsPerPage;
    }
    
    public CatalogBrowserScreen maxItemsPerPage(Integer maxItemsPerPage) {
        this.setMaxItemsPerPage(maxItemsPerPage);
        return this;
    }
    
    public void addCategory(ActionItem... categories) {
        this.getCategories().addAll(Arrays.asList(categories));
    }
    
    public void addCategory(ActionItem category) {
        this.getCategories().add(category);
    }
    
    public CatalogBrowserScreen categories(List<ActionItem> categories) {
        this.setCategories(categories);
        return this;
    }
    
    public List<ActionItem> getCategories() {
        return categories;
    }
    
    public void setCategories(List<ActionItem> categories) {
        this.categories = categories;
    }
    
    public CatalogBrowserScreen selectedItemQuantity(Integer selectedItemQuantity) {
        this.setSelectedItemQuantity(selectedItemQuantity);
        return this;
    }
    
    public Integer getSelectedItemQuantity() {
        return selectedItemQuantity;
    }
    
    public void setSelectedItemQuantity(Integer selectedItemQuantity) {
        this.selectedItemQuantity = selectedItemQuantity;
    }

    public Integer getItemStartIndex() {
        return itemStartIndex;
    }

    public void setItemStartIndex(Integer itemStartIndex) {
        this.itemStartIndex = itemStartIndex;
    }

    public CatalogBrowserScreen itemStartIndex(Integer itemStartIndex) {
        this.setItemStartIndex(itemStartIndex);
        return this;
    }
    
    public Integer getItemEndIndex() {
        return itemEndIndex;
    }

    public void setItemEndIndex(Integer itemEndIndex) {
        this.itemEndIndex = itemEndIndex;
    }

    public CatalogBrowserScreen itemEndIndex(Integer itemEndIndex) {
        this.setItemEndIndex(itemEndIndex);
        return this;
    }
    
    public void setItemTotalCount(Integer itemTotalCount) {
        this.itemTotalCount =itemTotalCount;
    }
    
    public Integer getItemTotalCount() {
        return this.itemTotalCount;
    }
    
    public CatalogBrowserScreen itemTotalCount(Integer itemTotalCount) {
        this.setItemTotalCount(itemTotalCount);
        return this;
    }
    
}
