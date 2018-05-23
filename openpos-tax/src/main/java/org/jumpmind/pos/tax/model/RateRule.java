package org.jumpmind.pos.tax.model;

import java.math.BigDecimal;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

/**
 * A rule denoting what percentage or dollar amount of tax is applied to a
 * particular taxable total in a RetailTransaction.
 * 
 * @author elong
 * 
 */
@Table(
        description = "A rule denoting what percentage or dollar amount of tax is applied to a particular taxable total in a RetailTransaction.")
public class RateRule extends Entity {
    
    public static final int TYPE_PERCENT_RATE = 1;
    public static final int TYPE_FLAT_RATE = 2;

    @Column(primaryKey = true)
    private String id;

    @Column(primaryKey = true)
    private String authorityId;

    @Column(primaryKey = true)
    private String groupId;

    @Column(primaryKey = true)
    private Integer rateRuleSequenceNumber;
    
    // TODO tax type
    // TODO tax holiday
    
    @Column
    private int typeCode;

    @Column()
    private BigDecimal minTaxableAmount;

    @Column()
    private BigDecimal maxTaxableAmount;
    
    @Column
    private BigDecimal taxPercent;

    @Column
    private BigDecimal taxAmount;
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String toString() {
        return getClass().getSimpleName() + " [" + rateRuleSequenceNumber + "] " + minTaxableAmount.setScale(2, BigDecimal.ROUND_HALF_UP)
                + " to " + maxTaxableAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getMaxTaxableAmount() {
        return maxTaxableAmount;
    }

    public void setMaxTaxableAmount(BigDecimal maxTaxableAmount) {
        this.maxTaxableAmount = maxTaxableAmount;
    }

    public BigDecimal getMinTaxableAmount() {
        return minTaxableAmount;
    }

    public void setMinTaxableAmount(BigDecimal minTaxableAmount) {
        this.minTaxableAmount = minTaxableAmount;
    }

    public Integer getRateRuleSequenceNumber() {
        return rateRuleSequenceNumber;
    }

    public void setRateRuleSequenceNumber(Integer rateRuleSequenceNumber) {
        this.rateRuleSequenceNumber = rateRuleSequenceNumber;
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

    public int getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(int typeCode) {
        this.typeCode = typeCode;
    }

    public BigDecimal getTaxPercent() {
        return taxPercent;
    }

    public void setTaxPercent(BigDecimal percent) {
        this.taxPercent = percent;
    }

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal amount) {
        this.taxAmount = amount;
    }
    
    

}
