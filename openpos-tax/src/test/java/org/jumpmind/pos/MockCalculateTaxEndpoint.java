package org.jumpmind.pos;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;

import org.jumpmind.pos.tax.model.Authority;
import org.jumpmind.pos.tax.model.Group;
import org.jumpmind.pos.tax.model.GroupRule;
import org.jumpmind.pos.tax.model.RateRule;
import org.jumpmind.pos.tax.model.TaxConstants;
import org.jumpmind.pos.tax.service.CalculateTaxEndpoint;

public class MockCalculateTaxEndpoint extends CalculateTaxEndpoint {

    @Override
    public Collection<Authority> getAuthorities(String storeId) {
        Collection<Authority> authorities = new ArrayList<Authority>();

        authorities.add(getFirstAuthority());
        authorities.add(getSecondAuthority());
        authorities.add(getThirdAuthority());

        return authorities;
    }

    private Authority getFirstAuthority() {
        Authority authority = new Authority();
        authority.setId("1");
        authority.setRoundingCode(TaxConstants.ROUNDING_HALF_UP);
        authority.setRoundingDigitsQuantity(new Integer(2));

        addPercentRule(authority, "100", 5.25, TaxConstants.CALCULATION_TRANSACTION);
        addPercentRule(authority, "101", 5.25, TaxConstants.CALCULATION_TRANSACTION);
        addPercentRule(authority, "102", 5.25, TaxConstants.CALCULATION_TRANSACTION);
        addPercentRule(authority, "200", 5.25, TaxConstants.CALCULATION_ITEM);
        addPercentRule(authority, "300", 5.25, TaxConstants.CALCULATION_ITEM_TRANSACTION);
        addFlatRule(authority, "400", 0.05, TaxConstants.CALCULATION_ITEM);
        addFlatRule(authority, "401", 0.10, TaxConstants.CALCULATION_ITEM);

        addPercentRule(authority, "450", 1.25, TaxConstants.CALCULATION_TRANSACTION);
        authority.getGroupRule("450").setCompoundSequenceNumber(1);

        // Florida 6.0%
        addTableRule(authority, "500", new double[] { 0, 0.10, 0.17, 0.34, 0.51, 0.67, 0.84, 1.10 }, 1.00,
                TaxConstants.CALCULATION_TRANSACTION);
        addTableRule(authority, "501", new double[] { 0, 0.10, 0.17, 0.34, 0.51, 0.67, 0.84, 1.10 }, 1.00,
                TaxConstants.CALCULATION_TRANSACTION);
        // Idaho 6.0%
        addTableRule(authority, "600", new double[] { 0, 0.12, 0.21, 0.38, 0.54, 0.71, 0.88 }, 0.50, TaxConstants.CALCULATION_TRANSACTION);

        return authority;
    }

    private Authority getSecondAuthority() {
        Authority authority = new Authority();
        authority.setId("2");
        authority.setRoundingCode(TaxConstants.ROUNDING_HALF_DOWN);
        authority.setRoundingDigitsQuantity(new Integer(2));

        addPercentRule(authority, "700", 7.75, TaxConstants.CALCULATION_TRANSACTION);

        return authority;
    }

    private Authority getThirdAuthority() {
        Authority authority = new Authority();
        authority.setId("3");
        authority.setRoundingCode(TaxConstants.ROUNDING_HALF_UP);
        authority.setRoundingDigitsQuantity(new Integer(2));

        addPercentRule(authority, "450", 5.25, TaxConstants.CALCULATION_TRANSACTION);
        authority.getGroupRule("450").setCompoundSequenceNumber(2);

        return authority;
    }

    private GroupRule addGroup(Authority authority, Group taxGroup, String method) {
        GroupRule groupRule = new GroupRule();
        groupRule.setAuthority(authority);
        groupRule.setAuthorityId(authority.getId());
        groupRule.setGroup(taxGroup);
        groupRule.setCalculationMethodCode(method);
        groupRule.setRateRuleUsageCode(TaxConstants.USAGE_PICK_ONE);
        authority.addGroupRule(groupRule);
        return groupRule;
    }

    private RateRule addPercentRule(Authority authority, String taxGroupId, double percent, String method) {
        Group taxGroup = new Group(taxGroupId);
        GroupRule groupRule = addGroup(authority, taxGroup, method);

        RateRule rateRule = new RateRule();
        rateRule.setTypeCode(RateRule.TYPE_PERCENT_RATE);
        rateRule.setTaxPercent(new BigDecimal(percent));
        groupRule.addRateRule(rateRule);
        return rateRule;
    }

    private RateRule addFlatRule(Authority authority, String taxGroupId, double amount, String method) {
        Group taxGroup = new Group(taxGroupId);
        GroupRule groupRule = addGroup(authority, taxGroup, method);

        RateRule rateRule = new RateRule();
        rateRule.setTypeCode(RateRule.TYPE_FLAT_RATE);
        rateRule.setTaxAmount(new BigDecimal(amount));
        groupRule.addRateRule(rateRule);
        return rateRule;
    }

    private void addTableRule(Authority authority, String taxGroupId, double[] breaks, double cycleAmount, String method) {
        Group taxGroup = new Group(taxGroupId);
        GroupRule groupRule = addGroup(authority, taxGroup, method);
        groupRule.setRateRuleUsageCode(TaxConstants.USAGE_TAX_TABLE);
        groupRule.setCycleAmount(new BigDecimal(cycleAmount));

        BigDecimal amount = BigDecimal.ZERO;
        BigDecimal penny = new BigDecimal("0.01");
        MathContext mc = new MathContext(2, RoundingMode.HALF_UP);
        for (int i = 0; i < breaks.length - 1; i++) {
            BigDecimal minTaxableAmount = new BigDecimal(breaks[i], mc);
            BigDecimal maxTaxableAmount = new BigDecimal(breaks[i + 1], mc).subtract(penny);
            RateRule rateRule = new RateRule();
            rateRule.setTypeCode(RateRule.TYPE_FLAT_RATE);
            rateRule.setMinTaxableAmount(minTaxableAmount);
            rateRule.setMaxTaxableAmount(maxTaxableAmount);
            rateRule.setTaxAmount(amount);
            groupRule.addRateRule(rateRule);
            amount = amount.add(penny);
        }
    }

}
