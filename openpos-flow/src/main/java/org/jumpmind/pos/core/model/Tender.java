package org.jumpmind.pos.core.model;

import org.jumpmind.pos.core.ui.ActionItem;

public class Tender extends Total {

    private ActionItem voidButton;

    public Tender(String name, String amount) {
        super(name, amount);
    }

    public Tender(String name, String amount, ActionItem voidButton) {
        super(name, amount);
        this.voidButton = voidButton;
    }

    public ActionItem getVoidButton() {
        return voidButton;
    }

    public void setVoidButton(ActionItem voidButton) {
        this.voidButton = voidButton;
    }
}
