package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutOptionsPart;

public class SelfCheckoutOptionsUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

    private String title;

    private String prompt;

    private SelfCheckoutOptionsPart selfCheckoutOptionsPart;

    private String imageUrl;

    private String icon;

    public SelfCheckoutOptionsUIMessage() {
        setScreenType(UIMessageType.SELF_CHECKOUT_OPTIONS);
    }

    public SelfCheckoutMenuPart getSelfCheckoutMenu() {
        return selfCheckoutMenu;
    }

    public void setSelfCheckoutMenu(SelfCheckoutMenuPart selfCheckoutMenu) {
        this.selfCheckoutMenu = selfCheckoutMenu;
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
