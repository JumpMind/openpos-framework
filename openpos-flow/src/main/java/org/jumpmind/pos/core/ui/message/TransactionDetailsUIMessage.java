package org.jumpmind.pos.core.ui.message;

import lombok.Getter;
import lombok.Setter;
import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.TransactionSummary;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransactionDetailsUIMessage extends UIMessage {

    private String providerKey;

    private TransactionSummary transactionHistory;
    private List<Total> totals;
    private Total grandTotal;
    private Total itemCount;

    private ActionItem checkoutButton;
    private UICustomer customer;
    private String statusMessage;

    private boolean enableCollapsibleItems = true;

    public TransactionDetailsUIMessage() {
        this.setScreenType(UIMessageType.TRANSACTION_DETAILS);
        this.setId("transactionDetails");
    }

    public void addTotal(Total total) {
        if (this.totals == null) {
            this.totals = new ArrayList<>();
        }
        this.totals.add(total);
    }

    public void addTotal(String name, String amount) {
        if (totals == null) {
            totals = new ArrayList<>();
        }
        totals.add(new Total(name, amount));
    }

    public void setGrandTotal(String name, String amount) {
        this.grandTotal = new Total(name, amount);
    }

}
