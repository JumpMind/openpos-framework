package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

public class PriceCheckerHomeUIMessage extends UIMessage {

    private String backgroundImageUrl;
    private ActionItem scanAction;

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

}
