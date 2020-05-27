package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class PromotionSearchUIMessage extends UIMessage {

    private String providerKey;

    private UICustomer customer;

    private String noPromotionsText;

    public PromotionSearchUIMessage() {
        this.setScreenType(UIMessageType.PROMOTION_SEARCH);
        this.setId("promotionSearch");
    }

    public String getProviderKey() {
        return providerKey;
    }

    public void setProviderKey(String providerKey) {
        this.providerKey = providerKey;
    }

    public UICustomer getCustomer() {
        return customer;
    }

    public void setCustomer(UICustomer customer) {
        this.customer = customer;
    }

    public String getNoPromotionsText() {
        return noPromotionsText;
    }

    public void setNoPromotionsText(String noPromotionsText) {
        this.noPromotionsText = noPromotionsText;
    }
}
