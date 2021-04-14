package org.jumpmind.pos.core.ui.data;

import lombok.Getter;
import lombok.Setter;
import org.jumpmind.pos.core.model.Total;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TransactionReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    private long transactionNumber;

    private String webOrderId;

    private List<TransactionReceiptLine> transactionInfoSection = new ArrayList<>();

    private List<TransactionReceiptLine> totalsInfoSection = new ArrayList<>();

    private Total transactionTotal;

    private String icon;

    private String barcode;

    public void addTransactionInfoLine(TransactionReceiptLine line) {
        this.transactionInfoSection.add(line);
    }

    public void addTotalsInfoLine(TransactionReceiptLine line) {
        this.totalsInfoSection.add(line);
    }
}
