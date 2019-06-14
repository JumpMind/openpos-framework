package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;

public class ListTransactionsScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    List<TransactionSummary> transactions = new ArrayList<>();
    
    public List<TransactionSummary> getTransactions() {
        return transactions;
    }
    
    public void setTransactions(List<TransactionSummary> transactions) {
        this.transactions = transactions;
    }

}
