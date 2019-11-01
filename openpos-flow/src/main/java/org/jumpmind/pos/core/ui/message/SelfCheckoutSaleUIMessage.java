package org.jumpmind.pos.core.ui.message;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.SellItem;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;

public class SelfCheckoutSaleUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

    private List<SellItem> items = new ArrayList<>();

    private List<Total> totals;
    private Total grandTotal;

    private boolean transactionActive = false;

    private ActionItem checkoutButton;
    private ActionItem logoutButton;
    private ActionItem loyaltyButton;
    private ActionItem promoButton;

    private UICustomer customer;

    private String prompt;

    private String backgroundImage;

    private boolean enableCollapsibleItems;

    public SelfCheckoutSaleUIMessage() {
        this.setScreenType(UIMessageType.SELF_CHECKOUT_SALE);
        this.setId("selfcheckout-sale");
        selfCheckoutMenu.setShowScan(true);
    }

    public SelfCheckoutMenuPart getSelfCheckoutMenu() {
        return selfCheckoutMenu;
    }

    public void setSelfCheckoutMenu(SelfCheckoutMenuPart selfCheckoutMenu) {
        this.selfCheckoutMenu = selfCheckoutMenu;
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

    public boolean isTransactionActive() {
        return transactionActive;
    }

    public void setTransactionActive(boolean transactionActive) {
        this.transactionActive = transactionActive;
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

    public ActionItem getLoyaltyButton() {
        return loyaltyButton;
    }

    public void setLoyaltyButton(ActionItem loyaltyButton) {
        this.loyaltyButton = loyaltyButton;
    }

    public ActionItem getPromoButton() {
        return promoButton;
    }

    public void setPromoButton(ActionItem promoButton) {
        this.promoButton = promoButton;
    }

    public UICustomer getCustomer() {
        return customer;
    }

    public void setCustomer(UICustomer customer) {
        this.customer = customer;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public boolean isEnableCollapsibleItems() {
        return enableCollapsibleItems;
    }

    public void setEnableCollapsibleItems(boolean enableCollapsibleItems) {
        this.enableCollapsibleItems = enableCollapsibleItems;
    }

}
