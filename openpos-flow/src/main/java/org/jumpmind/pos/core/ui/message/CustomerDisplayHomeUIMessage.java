package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class CustomerDisplayHomeUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String backgroundImage;

    public CustomerDisplayHomeUIMessage() {
        setId("customerdisplay-home");
        setScreenType(UIMessageType.CUSTOMER_DISPLAY_HOME);
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }
}
