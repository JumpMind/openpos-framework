package org.jumpmind.pos.core.ui.data;

import lombok.Data;
import org.jumpmind.pos.core.model.Total;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class TransactionReceipt implements Serializable {
    private static final long serialVersionUID = 1L;

    private long transactionNumber;
    private String webOrderId;
    private List<TransactionReceiptLine> transactionInfoSection = new ArrayList<>();
    private List<TransactionReceiptLine> totalsInfoSection = new ArrayList<>();
    private Total transactionTotal;
    private String icon;
    private List<TenderItem> tenderInfoSection;
    private String tenderInfoSectionTitle;
    private String barcode;

    public void addTransactionInfoLine(TransactionReceiptLine line) {
        if (this.transactionInfoSection == null) {
            this.transactionInfoSection = new ArrayList<>();
        }
        this.transactionInfoSection.add(line);
    }

    public void addTotalsInfoLine(TransactionReceiptLine line) {
        if (this.totalsInfoSection == null) {
            this.totalsInfoSection = new ArrayList<>();
        }
        this.totalsInfoSection.add(line);
    }
}
