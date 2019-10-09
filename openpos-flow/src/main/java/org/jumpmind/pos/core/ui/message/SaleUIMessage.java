package org.jumpmind.pos.core.ui.message;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.ActionItemGroup;
import org.jumpmind.pos.core.ui.data.SellItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

@AssignKeyBindings
public class SaleUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private List<ActionItem> sausageLinks;

    private String prompt;

    private List<SellItem> items = new ArrayList<>();

    private List<Total> totals;
    private Total grandTotal;

    private ActionItem checkoutButton;
    private ActionItem logoutButton;
    private ActionItem loyaltyButton;
    private ActionItem promoButton;

    private String itemCount;
    private boolean transactionActive = false;

    private UICustomer customer;

    private boolean locationEnabled;
    private String locationOverridePrompt;

    private String backgroundImage;

    public SaleUIMessage() {
        this.setScreenType(UIMessageType.SALE);
        this.setId("sale");
    }

    public List<ActionItem> getSausageLinks() {
        return sausageLinks;
    }

    public void setSausageLinks(List<ActionItem> sausageLinks) {
        this.sausageLinks = sausageLinks;
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

    public UICustomer getCustomer() {
        return customer;
    }

    public void setCustomer(UICustomer customer) {
        this.customer = customer;
    }

    public ActionItem getLoyaltyButton() {
        return loyaltyButton;
    }

    public void setLoyaltyButton(ActionItem loyaltyButton) {
        this.loyaltyButton = loyaltyButton;
    }

    public boolean isLocationEnabled() {
        return locationEnabled;
    }

    public void setLocationEnabled(boolean locationEnabled) {
        this.locationEnabled = locationEnabled;
    }

    public String getLocationOverridePrompt() {
        return locationOverridePrompt;
    }

    public void setLocationOverridePrompt(String locationOverridePrompt) {
        this.locationOverridePrompt = locationOverridePrompt;
    }

    public ActionItem getPromoButton() {
        return promoButton;
    }

    public void setPromoButton(ActionItem promoButton) {
        this.promoButton = promoButton;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public void setTransactionActive(boolean isTransactionActive) {
        this.transactionActive = isTransactionActive;
    }

    public boolean isTransactionActive() {
        return transactionActive;
    }

    public String getItemCount() {
        return itemCount;
    }

    public void setItemCount(String itemCount) {
        this.itemCount = itemCount;
    }

    public ActionItem getCheckoutButton() {
        return checkoutButton;
    }

    public void setCheckoutButton(ActionItem checkoutButton) {
        this.checkoutButton = checkoutButton;
    }

    public ActionItem getLogoutButton() {
        return logoutButton;
    }

    public void setLogoutButton(ActionItem logoutButton) {
        this.logoutButton = logoutButton;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
}
