package org.jumpmind.pos.core.model;

import java.util.List;

public class PopTartField extends ComboField {
    
    private static final long serialVersionUID = 1L;

    public PopTartField() {
        super();
        setInputType(FieldInputType.PopTart);
    }

    public PopTartField(String fieldId, String label, String placeholder, List<String> values) {
        super(fieldId, label, placeholder, values);
        setInputType(FieldInputType.PopTart);
    }

    public PopTartField(String fieldId, String label, String placeholder) {
        super(fieldId, label, placeholder);
        setInputType(FieldInputType.PopTart);
    }

    public PopTartField(String fieldId, String placeholder) {
        super(fieldId, placeholder);
        setInputType(FieldInputType.PopTart);
    }


}
