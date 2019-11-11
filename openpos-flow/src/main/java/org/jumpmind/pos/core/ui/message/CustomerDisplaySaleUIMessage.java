package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.SellItem;

import java.util.ArrayList;
import java.util.List;

public class CustomerDisplaySaleUIMessage extends SaleUIMessage {
    private static final long serialVersionUID = 1L;

    public CustomerDisplaySaleUIMessage() {
        this.setScreenType(UIMessageType.CUSTOMER_DISPLAY_SALE);
        this.setId("customerdisplay-sale");
    }

}
