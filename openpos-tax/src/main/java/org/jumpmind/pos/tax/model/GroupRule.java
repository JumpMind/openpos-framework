package org.jumpmind.pos.tax.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

/**
 * A rule that prescribes how a particular tax is to be applied to a group
 * Items.
 * 
 * @author elong
 * 
 */
@Table(description = "A rule that prescribes how a particular tax is to be applied to a group Items.")
public class GroupRule extends Entity implements Comparable<GroupRule> {

    @Column(primaryKey = true)
    private String id;

    @Column(primaryKey = true)
    private String authorityId;

    @Column(primaryKey = true)
    private String groupId;

    // TODO tax type?
    // TODO tax holiday flag?
    // TODO effective time
    // TODO customer group?

    @Column()
    private String ruleName;

    @Column()
    private String description;

    @Column()
    private Integer compoundSequenceNumber;

    @Column()
    private Boolean taxOnGrossAmountFlag;

    @Column()
    private String calculationMethodCode;

    @Column()
    private String rateRuleUsageCode;

    @Column()
    private BigDecimal cycleAmount;

    private Group group;

    private Collection<RateRule> rateRules;

    private Authority authority;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthorityId() {
        return authorityId;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public String toString() {
        return "GroupRule " + authorityId + "-" + group.getId() + "-" + compoundSequenceNumber;
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof GroupRule) {
            GroupRule groupRule = (GroupRule) o;
            return groupRule.getAuthorityId().equals(authorityId) && groupRule.getGroup().equals(group);
        }
        return false;
    }

    public int compareTo(GroupRule groupRule) {
        int compare = -1;
        if (groupRule != null) {
            compare = groupRule.getAuthorityId().compareTo(authorityId);
            if (compare == 0) {
                compare = groupRule.getGroup().compareTo(group);
            }
        }
        return compare;
    }

    public void addRateRule(RateRule rateRule) {
        if (rateRules == null) {
            rateRules = new ArrayList<RateRule>();
        }
        rateRules.add(rateRule);
    }

    public RateRule getFirstRateRule() {
        if (rateRules != null && rateRules.size() > 0) {
            return rateRules.iterator().next();
        }
        return null;
    }

    public RateRule getLastRateRule() {
        RateRule rateRule = null;
        if (rateRules != null) {
            Iterator<RateRule> iter = rateRules.iterator();
            while (iter.hasNext()) {
                rateRule = iter.next();
            }
        }
        return rateRule;
    }

    public BigDecimal getTaxPercent() {
        if (rateRules != null && rateRules.size() == 1) {
            RateRule rateRule = rateRules.iterator().next();
            if (rateRule.getTypeCode() == RateRule.TYPE_PERCENT_RATE) {
                return rateRule.getTaxPercent();
            }
        }
        return null;
    }

    public boolean usesCompounding() {
        return compoundSequenceNumber != null && compoundSequenceNumber.intValue() > 0;
    }

    public String getCalculationMethodCode() {
        return calculationMethodCode;
    }

    public void setCalculationMethodCode(String calculationMethodCode) {
        this.calculationMethodCode = calculationMethodCode;
    }

    public Integer getCompoundSequenceNumber() {
        return compoundSequenceNumber;
    }

    public void setCompoundSequenceNumber(Integer compoundSequenceNumber) {
        this.compoundSequenceNumber = compoundSequenceNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String name) {
        this.ruleName = name;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Boolean getTaxOnGrossAmountFlag() {
        return taxOnGrossAmountFlag;
    }

    public void setTaxOnGrossAmountFlag(Boolean taxOnGrossAmountFlag) {
        this.taxOnGrossAmountFlag = taxOnGrossAmountFlag;
    }

    public Collection<RateRule> getRateRules() {
        return rateRules;
    }

    public void setRateRules(Collection<RateRule> rateRules) {
        this.rateRules = rateRules;
    }

    public String getRateRuleUsageCode() {
        return rateRuleUsageCode;
    }

    public void setRateRuleUsageCode(String rateRuleUsageCode) {
        this.rateRuleUsageCode = rateRuleUsageCode;
    }

    public BigDecimal getCycleAmount() {
        return cycleAmount;
    }

    public void setCycleAmount(BigDecimal centsPerCycle) {
        this.cycleAmount = centsPerCycle;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Authority getAuthority() {
        return authority;
    }

}
