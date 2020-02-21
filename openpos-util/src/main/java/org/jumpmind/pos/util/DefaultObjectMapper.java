package org.jumpmind.pos.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.joda.money.Money;
import org.jumpmind.pos.util.web.MoneyDeserializer;
import org.jumpmind.pos.util.web.MoneySerializer;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

public class DefaultObjectMapper {
    
    public static ObjectMapper defaultObjectMapper() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Money.class, new MoneySerializer());
        module.addDeserializer(Money.class, new MoneyDeserializer());
        ObjectMapper mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
        dateFormat.setTimeZone(TimeZone.getDefault());
        mapper.setDateFormat(dateFormat);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.registerModule(module);
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    public static ObjectMapper build() {
        return defaultObjectMapper();
    }     
}
