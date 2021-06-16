package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String providerKey;

    private List<Total> totals;
    private Total grandTotal;
    private Total itemCount;

    private ActionItem checkoutButton;
    private ActionItem loyaltyButton;
    private ActionItem linkedCustomerButton;
    private ActionItem linkedEmployeeButton;

    private String loyaltyIDLabel;
    private String profileIcon;
    private List<UIMembership> memberships;
    private boolean membershipEnabled;
    private boolean customerMissingInfoEnabled;
    private boolean customerMissingInfo;
    private String customerMissingInfoIcon;
    private String customerMissingInfoLabel;
    private String memberIcon;
    private String nonMemberIcon;
    private String noMembershipsFoundLabel;
    private ActionItem mobileLoyaltyButton;

    private boolean transactionActive = false;

    private UICustomer customer;
    private UICustomer employee;

    private boolean locationEnabled;
    private String locationOverridePrompt;

    private boolean enableCollapsibleItems = true;

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
