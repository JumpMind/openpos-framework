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
public class SaleUIMessage extends TransactionUIMessage {
    private static final long serialVersionUID = 1L;

    private List<OrderSummary> orders;
    private ActionItem removeOrderAction;

    private ActionItem helpButton;
    private ActionItem logoutButton;
    private ActionItem promoButton;

    private AdditionalLabel taxExemptCertificateDetail;

    public SaleUIMessage() {
        this.setScreenType(UIMessageType.SALE);
        this.setId("sale");
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
    
}
