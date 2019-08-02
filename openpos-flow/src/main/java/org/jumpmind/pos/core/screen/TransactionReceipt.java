package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TransactionReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    private long transactionNumber;

    private String webOrderId;

    private List<TransactionReceiptLine> transactionInfoSection;

    private List<TransactionReceiptLine> totalsInfoSection;

    public long getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(long transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getWebOrderId() {
        return webOrderId;
    }

    public void setWebOrderId(String webOrderId) {
        this.webOrderId = webOrderId;
    }

    public List getTransactionInfoSection() {
        return transactionInfoSection;
    }

    public void setTransactionInfoSection(List<TransactionReceiptLine> transactionInfoSection) {
        this.transactionInfoSection = transactionInfoSection;
    }

    public void addTransactionInfoLine(TransactionReceiptLine line) {
        if(this.transactionInfoSection == null) {
            this.transactionInfoSection = new ArrayList<>();
        }
        this.transactionInfoSection.add(line);
    }

    public List getTotalsInfoSection() {
        return totalsInfoSection;
    }

    public void setTotalsInfoSection(List<TransactionReceiptLine> totalsInfoSection) {
        this.totalsInfoSection = totalsInfoSection;
    }

    public void addTotalsInfoLine(TransactionReceiptLine line) {
        if(this.totalsInfoSection == null) {
            this.totalsInfoSection = new ArrayList<>();
        }
        this.totalsInfoSection.add(line);
    }

}
