package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;

public class SelfCheckoutPromptUIMessage extends PromptWithOptionsUIMessage {

    private static final long serialVersionUID = 1L;

    public SelfCheckoutPromptUIMessage() {
        setScreenType(UIMessageType.SELF_CHECKOUT_PROMPT);
    }

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

    public SelfCheckoutMenuPart getSelfCheckoutMenu() {
        return selfCheckoutMenu;
    }

    public void setSelfCheckoutMenu(SelfCheckoutMenuPart selfCheckoutMenu) {
        this.selfCheckoutMenu = selfCheckoutMenu;
    }

}
