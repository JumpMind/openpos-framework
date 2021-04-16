package org.jumpmind.pos.core.model;

import lombok.Getter;
import lombok.Setter;
import org.jumpmind.pos.core.ui.ActionItem;

@Getter
@Setter
public class Tender extends Total {

    private ActionItem voidButton;

    private String cardNumber;

    public Tender(String name, String amount) {
        super(name, amount);
    }

    public Tender(String name, String amount, ActionItem voidButton) {
        super(name, amount);
        this.voidButton = voidButton;
    }
}
