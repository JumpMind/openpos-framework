package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.List;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.PageEvent;

public class CatalogBrowserForm implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private PageEvent pageEvent;
    private Form form;
    private ActionItem selectedCategory;
    private List<SellItem> selectedItems;
    
    public PageEvent getPageEvent() {
        return pageEvent;
    }
    public void setPageEvent(PageEvent pageEvent) {
        this.pageEvent = pageEvent;
    }
    public Form getForm() {
        return form;
    }
    public void setForm(Form form) {
        this.form = form;
    }
    public List<SellItem> getSelectedItems() {
        return selectedItems;
    }
    public void setSelectedItems(List<SellItem> selectedItems) {
        this.selectedItems = selectedItems;
    }
    public ActionItem getSelectedCategory() {
        return selectedCategory;
    }
    public void setSelectedCategory(ActionItem selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
    
    
}
