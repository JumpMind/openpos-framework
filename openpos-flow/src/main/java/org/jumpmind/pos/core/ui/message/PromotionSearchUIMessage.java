package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class PromotionSearchUIMessage extends UIMessage {

    private String providerKey;

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
}
