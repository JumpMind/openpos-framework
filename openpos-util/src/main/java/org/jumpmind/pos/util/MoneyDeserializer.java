package org.jumpmind.pos.util;

import java.io.IOException;

import org.joda.money.Money;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class MoneyDeserializer extends StdDeserializer<Money> {
    
    private static final long serialVersionUID = 1L;

    public MoneyDeserializer() {
        super(Money.class);
    }

    @Override
    public Money deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String value = jp.readValueAs(String.class);
        return Money.parse(value);
    }
}