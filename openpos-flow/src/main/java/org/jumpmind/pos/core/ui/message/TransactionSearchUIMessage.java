package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.TransactionSummary;

import java.util.List;

public class TransactionSearchUIMessage extends UIMessage {

    private List<TransactionSummary> transactions;

    private ActionItem searchButton;

    public TransactionSearchUIMessage() {
        this.setScreenType(UIMessageType.TRANSACTION_SEARCH);
        this.setId("transaction");
    }

    public List<TransactionSummary> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionSummary> transactions) {
        this.transactions = transactions;
    }

    public ActionItem getSearchButton() {
        return searchButton;
    }

    public void setSearchButton(ActionItem searchButton) {
        this.searchButton = searchButton;
    }
}
