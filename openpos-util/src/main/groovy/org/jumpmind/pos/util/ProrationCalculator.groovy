package org.jumpmind.pos.util

import java.math.RoundingMode

import org.joda.money.CurrencyUnit

class ProrationCalculator {
    CurrencyUnit currency;
    
    ProrationCalculator(String isoCurrencyCode) {
        this.currency = CurrencyUnit.of(isoCurrencyCode);
    }

    List<BigDecimal> prorate(BigDecimal amountToBeProrated, List<BigDecimal> existingAmounts) {
        List<BigDecimal> proratedAmounts = new ArrayList<BigDecimal>();
        
        MoneyCalculator moneyCalculator = new MoneyCalculator(currency.getCode());
        
        BigDecimal amountsTotal = 0.00;
        for (BigDecimal amount : existingAmounts) {
            amountsTotal += amount;
        }
        if (amountToBeProrated > amountsTotal) {
            throw new RuntimeException("Amount to be prorated is greater than the total amount");
        }
        
        BigDecimal proratedTotal = 0.00;
        for (BigDecimal amount : existingAmounts) {
            BigDecimal itemPct = amount.divide(amountsTotal,10, RoundingMode.HALF_UP);
            BigDecimal proratedAmount = amountToBeProrated * itemPct;
            proratedAmount = moneyCalculator.amount(proratedAmount);
            proratedAmounts.add(proratedAmount);
            proratedTotal += proratedAmount;
        }
        BigDecimal remainder = moneyCalculator.amount(amountToBeProrated - proratedTotal);
        if (remainder != 0) {
            BigDecimal lastAmount = proratedAmounts.get(proratedAmounts.size()-1);
            proratedAmounts.set(proratedAmounts.size()-1, moneyCalculator.amount(lastAmount + remainder));
        }
        return proratedAmounts;
    }
}
