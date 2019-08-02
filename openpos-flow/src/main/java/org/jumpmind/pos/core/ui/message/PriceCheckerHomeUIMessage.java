package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

public class PriceCheckerHomeUIMessage extends UIMessage {

    private String backgroundImageUrl;
    private ActionItem scanAction;
    private String message;

    public PriceCheckerHomeUIMessage(){
        setScreenType(UIMessageType.PRICE_CHECKER_HOME);
    }

    public String getBackgroundImageUrl() {
        return backgroundImageUrl;
    }

    public void setBackgroundImageUrl(String backgroundImageUrl) {
        this.backgroundImageUrl = backgroundImageUrl;
    }

    public ActionItem getScanAction() {
        return scanAction;
    }

    public void setScanAction(ActionItem scanAction) {
        this.scanAction = scanAction;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
