package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;

public class SelfCheckoutSaleUIMessage extends SaleUIMessage {

    private static final long serialVersionUID = 1L;

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

    private ActionItem logoutButton;

    private SelfCheckoutCustomer customer;

    private String prompt;

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

    public ActionItem getLogoutButton() {
        return logoutButton;
    }

    public void setLogoutButton(ActionItem logoutButton) {
        this.logoutButton = logoutButton;
    }

    public SelfCheckoutCustomer getCustomer() {
        return customer;
    }

    public void setCustomer(SelfCheckoutCustomer customer) {
        this.customer = customer;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }
}
