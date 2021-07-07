package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.TransactionReceipt;

import java.util.ArrayList;
import java.util.List;

@AssignKeyBindings
@Data
public class ReturnUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String providerKey;

    private List<Total> totals;
    private Total grandTotal;
    private List<Total> itemCounts;

    private List<TransactionReceipt> receipts = new ArrayList<>();

    private ActionItem checkoutButton;
    private ActionItem loyaltyButton;
    private ActionItem linkedCustomerButton;
    private ActionItem linkedEmployeeButton;
    private ActionItem removeReceiptAction;
    private String loyaltyIDLabel;

    private boolean transactionActive = false;

    private UICustomer customer;
    private UICustomer employee;

    private boolean locationEnabled;
    private String locationOverridePrompt;

    private boolean enableCollapsibleItems = true;

    private boolean loyaltySignupInProgressOnCustomerDisplay;
    private String loyaltySignupInProgressMessage;
    private String loyaltySignupInProgressDetails;
    private String loyaltySignupInProgressDetailsIcon;

    public ReturnUIMessage() {
        this.setScreenType(UIMessageType.RETURN);
        this.setId("returns");
    }

    public void addTotal(String name, String amount) {
        if (totals == null) {
            totals = new ArrayList<>();
        }
        totals.add(new Total(name, amount));
    }

    public void addReceipt(TransactionReceipt receipt) {
        if(this.receipts == null) {
            this.receipts = new ArrayList<>();
        }
        this.receipts.add(receipt);
    }

    public void addItemCount(Total total) {
        if (itemCounts == null) {
            itemCounts = new ArrayList<>();
        }
        itemCounts.add(total);
    }
}
