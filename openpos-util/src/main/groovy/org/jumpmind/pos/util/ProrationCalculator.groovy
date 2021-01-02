package org.jumpmind.pos.util

import groovy.transform.CompileStatic

import java.math.RoundingMode

import org.joda.money.CurrencyUnit

@CompileStatic
@SuppressWarnings('ClassJavadoc')
class ProrationCalculator {
    CurrencyUnit currency

    ProrationCalculator(String isoCurrencyCode) {
        this.currency = CurrencyUnit.of(isoCurrencyCode)
    }

    List<BigDecimal> prorate(BigDecimal amountToBeProrated, List<BigDecimal> existingAmounts) {
        List<BigDecimal> proratedAmounts = []

        MoneyCalculator moneyCalculator = new MoneyCalculator(currency.code)

        BigDecimal amountsTotal = BigDecimal.ZERO
        for (BigDecimal amount : existingAmounts) {
            amountsTotal += amount
        }
        if (amountToBeProrated > amountsTotal) {
            throw new IllegalStateException('Amount to be prorated is greater than the total amount')
        }

        BigDecimal proratedTotal = BigDecimal.ZERO
        for (BigDecimal amount : existingAmounts) {
            BigDecimal itemPct = amount.divide(amountsTotal, 5, RoundingMode.HALF_UP)
            BigDecimal proratedAmount = amountToBeProrated * itemPct
            proratedAmount = moneyCalculator.amount(proratedAmount)
            proratedAmounts.add(proratedAmount)
            proratedTotal += proratedAmount
        }
        BigDecimal remainder = moneyCalculator.amount(amountToBeProrated - proratedTotal)
        if (remainder != 0) {
            BigDecimal lastAmount = proratedAmounts.last()
            proratedAmounts.set(proratedAmounts.size() - 1, moneyCalculator.amount(lastAmount + remainder))
        }
        return proratedAmounts
    }
}
