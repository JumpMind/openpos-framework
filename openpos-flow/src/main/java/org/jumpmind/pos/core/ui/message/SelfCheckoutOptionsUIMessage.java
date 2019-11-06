package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;
import org.jumpmind.pos.core.ui.messagepart.OptionsListPart;

public class SelfCheckoutOptionsUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

    private String title;

    private String prompt;

    private OptionsListPart optionsList;

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

    public OptionsListPart getOptionsList() {
        return optionsList;
    }

    public void setOptionsList(OptionsListPart optionsList) {
        this.optionsList = optionsList;
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
