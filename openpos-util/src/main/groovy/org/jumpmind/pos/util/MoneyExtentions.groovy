package org.jumpmind.pos.util

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import org.joda.money.CurrencyUnit
import org.joda.money.Money
import org.springframework.beans.factory.InitializingBean
import org.springframework.stereotype.Component

@Component
class MoneyExtentions implements InitializingBean {
  
    static def $(amount) {
        CurrencyUnit usd = CurrencyUnit.of("USD");
        org.joda.money.Money.of(usd, amount);
    }
    
    void afterPropertiesSet() {
        Money.metaClass.multiply { int value ->
            multipliedBy(value);
        }
        Money.metaClass.plus { BigDecimal amountToAdd ->
            plus(amountToAdd, java.math.RoundingMode.HALF_UP);    
        }
    } 
    
}
