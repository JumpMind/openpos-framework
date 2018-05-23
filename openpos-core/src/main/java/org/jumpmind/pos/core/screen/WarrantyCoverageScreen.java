package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class WarrantyCoverageScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private List<IItem> warrantyItems = new ArrayList<>();
    private String warrantyCostTotal;
    private SelectionMode selectionMode = SelectionMode.Multiple;
    private String text;
    
    public WarrantyCoverageScreen() {
        setType(ScreenType.WarrantyCoverage);
    }

    public List<IItem> getWarrantyItems() {
        return warrantyItems;
    }


    public void setWarrantyItems(List<IItem> warrantyItems) {
        this.warrantyItems = warrantyItems;
    }


    public void addWarrantyItem(IItem warrantyItem) {
        this.warrantyItems.add(warrantyItem);
    }

    public void clearWarrantyItems() {
        this.warrantyItems.clear();
    }
    
    public String getWarrantyCostTotal() {
        return warrantyCostTotal;
    }

    public void setWarrantyCostTotal(String warrantyCostTotal) {
        this.warrantyCostTotal = warrantyCostTotal;
    }
    
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }
    
    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
}
