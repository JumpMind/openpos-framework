package org.jumpmind.pos.core.ui.message;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import org.jumpmind.pos.core.model.Total;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.AdditionalLabel;
import org.jumpmind.pos.core.ui.data.OrderSummary;

@AssignKeyBindings
@Data
public class SaleUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String providerKey;

    private List<Total> totals;
    private Total grandTotal;
    private Total itemCount;
    

    private List<OrderSummary> orders;
    private ActionItem removeOrderAction;

    private ActionItem checkoutButton;
    private ActionItem helpButton;
    private ActionItem logoutButton;
    private ActionItem loyaltyButton;
    private ActionItem mobileLoyaltyButton;
    private ActionItem linkedCustomerButton;
    private ActionItem linkedEmployeeButton;
    private ActionItem promoButton;

    private boolean transactionActive = false;

    private UICustomer customer;
    private UICustomer employee;
    private AdditionalLabel taxExemptCertificateDetail;

    private boolean locationEnabled;
    private String locationOverridePrompt;

    private boolean enableCollapsibleItems = true;

    public SaleUIMessage() {
        this.setScreenType(UIMessageType.SALE);
        this.setId("sale");
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

    public void addOrder(OrderSummary orderSummary) {
        if(this.orders == null) {
            this.orders = new ArrayList<>();
        }
        this.orders.add(orderSummary);
    }

    public void setTaxExemptCertificateDetail(String label, String value) {
        this.taxExemptCertificateDetail = new AdditionalLabel(label, value);
    }

    public void setItemCount(String name, String amount) {
        this.setItemCount(new Total(name, amount));
    }
    
}
