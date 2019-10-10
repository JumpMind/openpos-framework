package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutOptionsPart;

public class CustomerDisplayOptionsUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    private String title;

    private String prompt;

    private SelfCheckoutOptionsPart selfCheckoutOptionsPart;

    private String imageUrl;

    private String icon;

    public CustomerDisplayOptionsUIMessage() {
        setScreenType(UIMessageType.CUSTOMER_DISPLAY_OPTIONS);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public SelfCheckoutOptionsPart getSelfCheckoutOptionsPart() {
        return selfCheckoutOptionsPart;
    }

    public void setSelfCheckoutOptionsPart(SelfCheckoutOptionsPart selfCheckoutOptionsPart) {
        this.selfCheckoutOptionsPart = selfCheckoutOptionsPart;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}
