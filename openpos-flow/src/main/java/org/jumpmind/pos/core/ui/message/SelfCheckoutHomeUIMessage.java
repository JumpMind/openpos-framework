package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class SelfCheckoutHomeUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    public SelfCheckoutHomeUIMessage() {
        setId("selfcheckout-home");
        setScreenType(UIMessageType.SELF_CHECKOUT_HOME);
    }
}
