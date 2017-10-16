package org.jumpmind.jumppos.core.screen;

import java.io.Serializable;

public class Transaction implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String transactionNumber;
    private Boolean active;    

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
    
    
}
