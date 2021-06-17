package org.jumpmind.pos.core.util;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.util.AppUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;

@Component
@Scope("prototype")
@Slf4j
public class LogFormatter {
    
    public static String[] SENSITIVE_FIELDS = new String[] {
            "password", "account", "cid", "creditcard", "unlock",
            "cardNumber", "driverLicense", "pinblock", "routingnumber", "walletidentifier", 
            "emvdata", "track1", "track2", "track3", "approvalcode", "ksnblock", "cardexpirydate", "referralnum",
            "ecomtoken", "issuernumber", "socialsec", "driverslicense", "lockoverride" };

    
    private ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(Include.NON_NULL);
    
    @PostConstruct
    public void init() {        
        if (AppUtils.isDevMode()) {
            log.info("Running in DEV mode, log output will NOT be redecated.");
            return;
        }
        
        mapper.registerModule(new SimpleModule() {
            private static final long serialVersionUID = 1L;

            @Override
            public void setupModule(SetupContext context) {
                super.setupModule(context);
                context.addBeanSerializerModifier(new BeanSerializerModifier() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public JsonSerializer<?> modifySerializer(
                            SerializationConfig config, BeanDescription desc, JsonSerializer<?> serializer) {
                        if (Action.class.isAssignableFrom(desc.getBeanClass())) {
                            return new ActionSerializer((JsonSerializer<Object>) serializer);
                        }
                        return serializer;
                    }
                });
            }
        });
    }

    public String toJsonString(Object o) {
        
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        
        if (o == null) {
            return "null";
        }
        try {
            return writer.writeValueAsString(o);
        } catch (JsonProcessingException ex) {
            log.warn("Could not serialize object for logging: " + o, ex);
            return "";
        }
    }
}
