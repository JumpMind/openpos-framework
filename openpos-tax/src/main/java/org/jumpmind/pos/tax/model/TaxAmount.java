package org.jumpmind.pos.tax.model;

import java.math.BigDecimal;

public class TaxAmount {

    private String authorityId;

    private String groupId;

    private BigDecimal taxableAmount;

    private BigDecimal taxAmount;

    private BigDecimal taxPercent;

    public TaxAmount() {
    }

    public TaxAmount(String authorityId, String groupId, BigDecimal taxableAmount, BigDecimal taxAmount, BigDecimal taxPercent) {
        this.authorityId = authorityId;
        this.groupId = groupId;
        this.taxableAmount = taxableAmount;
        this.taxAmount = taxAmount;
        this.taxPercent = taxPercent;
    }

    public TaxAmount(TaxAmount amount) {
        authorityId = amount.getAuthorityId();
        groupId = amount.getGroupId();
        taxableAmount = amount.getTaxableAmount();
        taxAmount = amount.getTaxAmount();
        taxPercent = amount.getTaxPercent();
    }

    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public BigDecimal getTaxableAmount() {
        return taxableAmount;
    }

    public void setTaxableAmount(BigDecimal taxableAmount) {
        this.taxableAmount = taxableAmount;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal taxPercent) {
        this.taxPercent = taxPercent;
    }

    public void add(TaxAmount itemTaxAmount) {
        if (taxableAmount == null) {
            taxableAmount = BigDecimal.ZERO;
        }
        if (taxAmount == null) {
            taxAmount = BigDecimal.ZERO;
        }
        taxableAmount = taxableAmount.add(itemTaxAmount.getTaxAmount());
        taxAmount = taxAmount.add(itemTaxAmount.getTaxAmount());
    }

}
