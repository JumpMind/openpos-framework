package org.jumpmind.pos.core.screen;

import java.io.Serializable;

public class TransactionSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    String transactionId;

    String description;

    String grandTotal;

    String workstationId;
    
    String storeNumber;
    
    String tillId;
    
    String location;
    
    String transactionDate;
    
    int numberUnitsSold;
    
    String rewardsId;
    
    String customerName;

    public TransactionSummary() {
    }

    public TransactionSummary(String transactionId, String description, String grandTotal, String workstationId) {
        this.transactionId = transactionId;
        this.description = description;
        this.grandTotal = grandTotal;
        this.workstationId = workstationId;
    }

    public TransactionSummary(String transactionId, String grandTotal, String workstationId, String tillId, String rewardsId, String customerName) {
        this.transactionId = transactionId;
        this.grandTotal = grandTotal;
        this.workstationId = workstationId;
        this.tillId = tillId;
        this.rewardsId = rewardsId;
        this.customerName = customerName;
    }

    public void setWorkstationId(String workstationId) {
        this.workstationId = workstationId;
    }

    public String getWorkstationId() {
        return workstationId;
    }

    public void setTillId(String tillId) {
        this.tillId = tillId;
    }

    public String getTillId() {
        return tillId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        this.grandTotal = grandTotal;
    }
    
    public String getStoreNumber() {
        return storeNumber;
    }
    
    public void setStoreNumber(String storeNumber) {
        this.storeNumber = storeNumber;
    }
    
    public String getLocation() {
        return location;
    }
    
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public int getNumberUnitsSold() {
        return numberUnitsSold;
    }
    
    public void setNumberUnitsSold(int numberUnitsSold) {
        this.numberUnitsSold = numberUnitsSold;
    }

    public void setRewardsId(String rewardsId) {
        this.rewardsId = rewardsId;
    }

    public String getRewardsId() {
        return rewardsId;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerName() {
        return customerName;
    }
}
