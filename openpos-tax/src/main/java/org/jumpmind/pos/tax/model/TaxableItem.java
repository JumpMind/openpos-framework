package org.jumpmind.pos.tax.model;

import java.math.BigDecimal;

public class TaxableItem {

    private int sequenceNumber;

    private String groupId;

    private BigDecimal extendedAmount = BigDecimal.ZERO;

    private boolean taxExemptEligible = false;

    public TaxableItem() {
    }

    public TaxableItem(int sequenceNumber, String groupId, BigDecimal extendedAmount, boolean taxExemptEligible) {
        this.sequenceNumber = sequenceNumber;
        this.groupId = groupId;
        this.extendedAmount = extendedAmount;
        this.taxExemptEligible = taxExemptEligible;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getExtendedAmount() {
        return extendedAmount;
    }

    public void setExtendedAmount(BigDecimal extendedAmount) {
        this.extendedAmount = extendedAmount;
    }

    public boolean isTaxExemptEligible() {
        return taxExemptEligible;
    }

    public void setTaxExemptEligible(boolean taxExemptEligible) {
        this.taxExemptEligible = taxExemptEligible;
    }

}
