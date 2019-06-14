package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;

public class SelfCheckoutPromptUIMessage extends PromptWithOptionsUIMessage {

    private static final long serialVersionUID = 1L;

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

    private String imageUrl;

    public SelfCheckoutPromptUIMessage() {
        setScreenType(UIMessageType.SELF_CHECKOUT_PROMPT);
    }

    public SelfCheckoutMenuPart getSelfCheckoutMenu() {
        return selfCheckoutMenu;
    }

    public void setSelfCheckoutMenu(SelfCheckoutMenuPart selfCheckoutMenu) {
        this.selfCheckoutMenu = selfCheckoutMenu;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
