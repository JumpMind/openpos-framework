package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.message.PromptWithOptionsUIMessage;
import org.jumpmind.pos.core.ui.message.UIMessageType;

public class CustomerDisplayPromptUIMessage extends PromptWithOptionsUIMessage {

    private static final long serialVersionUID = 1L;

    private String imageUrl;

    public CustomerDisplayPromptUIMessage() {
        setScreenType(UIMessageType.CUSTOMER_DISPLAY_PROMPT);
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}