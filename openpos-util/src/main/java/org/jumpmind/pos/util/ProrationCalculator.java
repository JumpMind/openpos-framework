package org.jumpmind.pos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

public class ProrationCalculator {
	
    CurrencyUnit currency;
    
    public ProrationCalculator(String isoCurrencyCode) {
        this.currency = CurrencyUnit.of(isoCurrencyCode);
    }

    public List<BigDecimal> prorate(BigDecimal amountToBeProrated, List<BigDecimal> existingAmounts) {
        List<BigDecimal> proratedAmounts = new ArrayList<BigDecimal>();
        
        MoneyCalculator moneyCalculator = new MoneyCalculator(currency.getCode());
        
        BigDecimal amountsTotal = new BigDecimal(0);
        for (BigDecimal amount:existingAmounts) {
            amountsTotal = amountsTotal.add(amount);
        }
        if (amountToBeProrated.compareTo(amountsTotal) > 0) {
            throw new RuntimeException("Amount to be prorated is greater than the total amount");
        } 
        BigDecimal proratedTotal = new BigDecimal(0);
        for (BigDecimal amount:existingAmounts) {
            BigDecimal itemPct = amount.divide(amountsTotal,4, RoundingMode.HALF_UP);
            BigDecimal proratedAmount = amountToBeProrated.multiply(itemPct);
            proratedAmount = moneyCalculator.amount(proratedAmount);
            proratedAmounts.add(proratedAmount);
            proratedTotal = proratedTotal.add(proratedAmount);
        }
        BigDecimal remainder = moneyCalculator.amount(amountToBeProrated.subtract(proratedTotal));
        if (remainder.compareTo(new BigDecimal(0))!= 0) {
            BigDecimal lastAmount = proratedAmounts.get(proratedAmounts.size()-1);
            proratedAmounts.set(proratedAmounts.size()-1, moneyCalculator.amount(lastAmount.add(remainder)));
        }
        return proratedAmounts;
    }
}
