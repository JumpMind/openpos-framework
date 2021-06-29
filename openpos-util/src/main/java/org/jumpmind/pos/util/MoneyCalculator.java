package org.jumpmind.pos.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.MoneyUtils;

public class MoneyCalculator {

    CurrencyUnit currency;
    
    public MoneyCalculator(String isoCurrencyCode) {
        this.currency = CurrencyUnit.of(isoCurrencyCode);
    }
    
    public static Money zeroDefault() {
        return Money.zero(CurrencyUnit.USD);
    }
    
    public BigDecimal zero() {
        return Money.zero(currency).getAmount();
    }
    
    public BigDecimal amount(BigDecimal amount) {
        return money(amount).getAmount();
    }
    
    public Money money(double amount) {
        return money(new BigDecimal(amount));
    }
    
    public static Money zero(String currencyId) {
        return money(currencyId, BigDecimal.ZERO);
    }

    public static Money moneyUsd(double d) {
        return money("USD", new BigDecimal(d), RoundingMode.HALF_UP);
    }

    public static Money money(String currencyId, double d) {
        return money(currencyId, new BigDecimal(d), RoundingMode.HALF_UP);
    }
    
    public static Money money(String currencyId, BigDecimal value) {
        return money(currencyId, value, RoundingMode.HALF_UP);
    }

    public static Money money(String currencyId, String value) {
        return money(currencyId, new BigDecimal(value));
    }
    public static Money money(String currencyId, BigDecimal value, RoundingMode roundingMode) {
        return Money.of(CurrencyUnit.of(currencyId), value, roundingMode);
    }
    
    public static boolean isZero(Money money) {
        return money.getAmount().signum() == 0;
    }    
    
    public Money money(BigDecimal amount) {
        if (amount != null) {
            return Money.of(currency, amount, RoundingMode.HALF_UP);
        } else {
            return Money.zero(currency);
        }
    }
    
    public BigDecimal add(BigDecimal amount1, BigDecimal amount2) {
        return MoneyUtils.add(money(amount1), money(amount2)).getAmount();
    }    
    
    public BigDecimal subtract(BigDecimal amount1, BigDecimal amount2) {
        return MoneyUtils.subtract(money(amount1), money(amount2)).getAmount();
    }

    public Money subtract(Money amount1, Money amount2) {
    		return MoneyUtils.subtract(amount1, amount2);
    }
    
    public Money add(Money amount1, Money amount2) {
    		return MoneyUtils.add(amount1,  amount2);
    }
}
