package org.jumpmind.pos.util;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormField;
import org.jumpmind.pos.core.model.IFormElement;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ActionSerializer extends JsonSerializer<Action> {
    
    private JsonSerializer<Object> defaultSerializer;
    
    private final String MASKED_STRING = "********************"; 
    
    public ActionSerializer(JsonSerializer<Object> serializer) {
        this.defaultSerializer = serializer;
    }

    @Override
    public void serialize(Action value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {

        Action clone = ObjectUtils.deepClone(value);
        
        if (StringUtils.containsAny(clone.getName().toLowerCase(), LogFormatter.SENSITIVE_FIELDS)) {
            if (clone.getData() instanceof String) {
                clone.setData(MASKED_STRING);
            }
        }
        
        if (clone.getData() instanceof Form) {
            Form form = clone.getData();
            for (IFormElement formElement : form.getFormElements()) {
                if (formElement instanceof FormField) {
                    FormField field = (FormField) formElement;
                    
                    String fieldId = field.getId() != null ? field.getId().toLowerCase() : "";
                    String fieldLabel = field.getLabel() != null ? field.getLabel().toLowerCase() : "";
                    
                    if (field.isSensitive()
                            || StringUtils.containsAny(fieldId, LogFormatter.SENSITIVE_FIELDS)
                            || StringUtils.containsAny(fieldLabel, LogFormatter.SENSITIVE_FIELDS)
                            || field.getInputType() == FieldInputType.AlphanumericPassword) {
                        field.setValue(MASKED_STRING);
                    }
                }
            }
        }
        
        defaultSerializer.serialize(clone, gen, serializers);
    }

}
