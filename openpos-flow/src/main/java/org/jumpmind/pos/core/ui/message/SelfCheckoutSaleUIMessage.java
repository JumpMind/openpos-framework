package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;

public class SelfCheckoutSaleUIMessage extends SaleUIMessage {

    private static final long serialVersionUID = 1L;

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

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

}
