package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class ChangeScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private List<TenderItem> items = new ArrayList<>();
    
    private String total;
    private String tendered;
    private String balanceDue;
    
    public ChangeScreen() {
        setScreenType(ScreenType.Change);
    }
    
    public String getTotal() {
        return total;
    }
    
    public void setTotal(String total) {
        this.total = total;
    }
    
    public String getTendered() {
        return tendered;
    }
    
    public void setTendered(String tendered) {
        this.tendered = tendered;
    }
    
    public String getBalanceDue() {
        return balanceDue;
    }
    
    public void setBalanceDue(String balanceDue) {
        this.balanceDue = balanceDue;
    }

    public List<TenderItem> getItems() {
        return items;
    }

    public void setItems(List<TenderItem> items) {
        this.items = items;
    }
    
}
