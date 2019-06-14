package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.math.BigDecimal;

public class TransactionReceipt implements Serializable {

    private static final long serialVersionUID = 1L;

    private long transactionNumber;

    private String businessDate;

    private int items;

    private BigDecimal total;

    public long getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(long transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getBusinessDate() {
        return businessDate;
    }

    public void setBusinessDate(String businessDate) {
        this.businessDate = businessDate;
    }

    public int getItems() {
        return items;
    }

    public void setItems(int items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

}
