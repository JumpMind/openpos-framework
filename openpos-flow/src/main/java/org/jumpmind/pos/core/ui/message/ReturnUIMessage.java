package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.data.TransactionReceipt;

import java.util.ArrayList;
import java.util.List;

@AssignKeyBindings
@Data
public class ReturnUIMessage extends TransactionUIMessage {
    private static final long serialVersionUID = 1L;

    private List<TransactionReceipt> receipts = new ArrayList<>();
    private ActionItem removeReceiptAction;

    public ReturnUIMessage() {
        this.setScreenType(UIMessageType.RETURN);
        this.setId("returns");
    }

    public void addReceipt(TransactionReceipt receipt) {
        if(this.receipts == null) {
            this.receipts = new ArrayList<>();
        }
        this.receipts.add(receipt);
    }
}
