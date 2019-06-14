package org.jumpmind.pos.core.ui.message;

import java.util.List;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutMenuPart;
import org.jumpmind.pos.core.ui.messagepart.SelfCheckoutOptionsPart;

public class SelfCheckoutTenderUIMessage extends UIMessage {

    private static final long serialVersionUID = 1L;

    private SelfCheckoutMenuPart selfCheckoutMenu = new SelfCheckoutMenuPart();

    private String title;

    private String prompt;

    private Total amountDue;

    private List<Total> amounts;

    private SelfCheckoutOptionsPart selfCheckoutOptionsPart;

    private String imageUrl;

    public SelfCheckoutTenderUIMessage() {
        setScreenType(UIMessageType.SELF_CHECKOUT_TENDER);
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

    public Total getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(Total amountDue) {
        this.amountDue = amountDue;
    }

    public List<Total> getAmounts() {
        return amounts;
    }

    public void setAmounts(List<Total> amounts) {
        this.amounts = amounts;
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
}
