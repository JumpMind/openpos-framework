package org.jumpmind.pos.tax.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

/**
 * A government authority that levies sales taxes and on whose behalf the store
 * collects these sales taxes.
 * 
 * @author elong
 * 
 */
@Table(description = "A government authority that levies sales taxes and on whose behalf the store collects these sales taxes.")
public class Authority extends Entity implements Comparable<Authority> {

    @Column(primaryKey = true)
    private String id;

    @Column
    private String authName;

    @Column
    private String roundingCode;

    @Column
    private Integer roundingDigitsQuantity;

    private Collection<GroupRule> groupRules;

    public Authority() {
    }

    public Authority(String id) {
        this.id = id;
    }

    public String toString() {
        return getClass().getSimpleName() + " " + id + ": " + authName;
    }

    public boolean equals(Object o) {
        if (o != null && o instanceof Authority) {
            Authority authority = (Authority) o;
            return authority.getId().equals(id);
        }
        return false;
    }

    public int compareTo(Authority o) {
        if (o != null && o instanceof Authority) {
            Authority authority = (Authority) o;
            return authority.getId().compareTo(id);
        }
        return -1;
    }

    public void addGroupRule(GroupRule groupRule) {
        if (groupRules == null) {
            groupRules = new ArrayList<GroupRule>();
        }
        groupRules.add(groupRule);
    }

    public GroupRule getGroupRule(String groupId) {
        for (GroupRule groupRule : groupRules) {
            if (groupRule.getGroup().getId().equals(groupId)) {
                return groupRule;
            }
        }
        return null;
    }

    public BigDecimal round(BigDecimal amount) {
        int digits = 2;
        if (roundingDigitsQuantity != null && roundingDigitsQuantity.intValue() >= 2) {
            digits = getRoundingDigitsQuantity();
        }

        RoundingMode mode = RoundingMode.HALF_UP;
        if (roundingCode != null) {
            if (roundingCode.equals(TaxConstants.ROUNDING_UP)) {
                mode = RoundingMode.HALF_UP;
            } else if (roundingCode.equals(TaxConstants.ROUNDING_DOWN)) {
                mode = RoundingMode.DOWN;
            } else if (roundingCode.equals(TaxConstants.ROUNDING_HALF_DOWN)) {
                mode = RoundingMode.HALF_DOWN;
            }
        }

        return amount.setScale(digits, mode);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthName() {
        return authName;
    }

    public void setAuthName(String name) {
        this.authName = name;
    }

    public String getRoundingCode() {
        return roundingCode;
    }

    public void setRoundingCode(String roundingCode) {
        this.roundingCode = roundingCode;
    }

    public Integer getRoundingDigitsQuantity() {
        return roundingDigitsQuantity;
    }

    public void setRoundingDigitsQuantity(Integer roundingDigitsQuantity) {
        this.roundingDigitsQuantity = roundingDigitsQuantity;
    }

    public Collection<GroupRule> getGroupRules() {
        return groupRules;
    }

    public void setGroupRules(Collection<GroupRule> groupRules) {
        this.groupRules = groupRules;
    }

}
