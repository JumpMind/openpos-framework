package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.TransactionSummary;

import java.util.List;

public class TransactionSearchUIMessage extends UIMessage {

    private ActionItem searchButton;
    private ActionItem clearButton;

    private String providerKey;
    private String noResultsMessage;

    public TransactionSearchUIMessage() {
        this.setScreenType(UIMessageType.TRANSACTION_SEARCH);
        this.setId("transaction");
    }

    public ActionItem getSearchButton() {
        return searchButton;
    }

    public void setSearchButton(ActionItem searchButton) {
        this.searchButton = searchButton;
    }

    public ActionItem getClearButton() {
        return clearButton;
    }

    public void setClearButton(ActionItem clearButton) {
        this.clearButton = clearButton;
    }

    public String getProviderKey() {
        return providerKey;
    }

    public void setProviderKey(String providerKey) {
        this.providerKey = providerKey;
    }

    public String getNoResultsMessage() {
        return noResultsMessage;
    }

    public void setNoResultsMessage(String noResultsMessage) {
        this.noResultsMessage = noResultsMessage;
    }

}
