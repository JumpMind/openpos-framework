package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.SellItem;

import java.util.ArrayList;
import java.util.List;

public class CustomerDisplaySaleUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private List<SellItem> items = new ArrayList<>();

    private List<Total> totals;

    private Total grandTotal;

    private ActionItem loyaltyButton;

    private UICustomer customer;

    private boolean enableCollapsibleItems = true;

    public CustomerDisplaySaleUIMessage() {
        this.setScreenType(UIMessageType.CUSTOMER_DISPLAY_SALE);
        this.setId("customerdisplay-sale");
    }

    public List<SellItem> getItems() {
        return items;
    }

    public void setItems(List<SellItem> items) {
        this.items = items;
    }

    public List<Total> getTotals() {
        return totals;
    }

    public void setTotals(List<Total> totals) {
        this.totals = totals;
    }

    public void addTotal(String name, String amount) {
        if (totals == null) {
            totals = new ArrayList<>();
        }
        totals.add(new Total(name, amount));
    }

    public Total getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(Total grandTotal) {
        this.grandTotal = grandTotal;
    }

    public void setGrandTotal(String name, String amount) {
        this.grandTotal = new Total(name, amount);
    }

    public ActionItem getLoyaltyButton() {
        return loyaltyButton;
    }

    public void setLoyaltyButton(ActionItem loyaltyButton) {
        this.loyaltyButton = loyaltyButton;
    }

    public UICustomer getCustomer() {
        return customer;
    }

    public void setCustomer(UICustomer customer) {
        this.customer = customer;
    }

    public boolean isEnableCollapsibleItems() {
        return enableCollapsibleItems;
    }

    public void setEnableCollapsibleItems(boolean enableCollapsibleItems) {
        this.enableCollapsibleItems = enableCollapsibleItems;
    }

}
