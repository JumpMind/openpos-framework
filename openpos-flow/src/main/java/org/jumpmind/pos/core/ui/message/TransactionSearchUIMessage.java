package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class TransactionSearchUIMessage extends UIMessage {

    private ActionItem searchButton;
    private ActionItem clearButton;

    private String providerKey;
    private String noResultsMessage;

    public TransactionSearchUIMessage() {
        this.setScreenType(UIMessageType.TRANSACTION_SEARCH);
        this.setId("transaction");
    }

}
