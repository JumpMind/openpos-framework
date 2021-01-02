package org.jumpmind.pos.util

import groovy.transform.CompileDynamic
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@CompileDynamic
@SuppressWarnings('ClassJavadoc')
@Component
class MoneyExtensions implements InitializingBean {

    @SuppressWarnings('MethodName')
    static Money $(String amount) {
        CurrencyUnit usd = CurrencyUnit.of('USD')
        Money.of(usd, amount)
    }

    void afterPropertiesSet() {
        Money.metaClass * { int value ->
            multipliedBy(value)
        }
        Money.metaClass + { BigDecimal amountToAdd ->
            plus(amountToAdd, java.math.RoundingMode.HALF_UP)
        }
    }

}
