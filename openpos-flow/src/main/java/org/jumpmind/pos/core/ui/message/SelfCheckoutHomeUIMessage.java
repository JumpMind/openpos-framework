package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class SelfCheckoutHomeUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    private String backgroundImage;

    private String prompt;

    private String imageUrl;

    public SelfCheckoutHomeUIMessage() {
        setId("selfcheckout-home");
        setScreenType(UIMessageType.SELF_CHECKOUT_HOME);
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
