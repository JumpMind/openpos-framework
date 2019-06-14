package org.jumpmind.pos.core.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;

public class FormDisplayField implements IFormElement, IField, Serializable {
    private static final long serialVersionUID = 1L;
    
    public enum ValueDisplayMode {
        Single,
        Multiple
    }
    
    /**
     * Put properties in this map if they are optional. When not set, they don't
     * show up in the json which means less overhead.
     */
    private Map<String, Object> optionalProperties = new HashMap<String, Object>();    

    private FieldElementType elementType = FieldElementType.Display;
    private String label;
    private String fieldId;
    private List<String> values = new ArrayList<String>();
    private ValueDisplayMode valueDisplayMode = ValueDisplayMode.Single;
        
    public FormDisplayField() {
    }
    
    public FormDisplayField(FieldElementType elementType) {
        this.elementType = elementType;
    }
    
    public FormDisplayField(String label, String fieldId, String value) {
        this.label = label;
        this.fieldId = fieldId;
        this.setValue(value);
    }

    public FormDisplayField(String label, String fieldId, String... values) {
        this(label, fieldId, values != null ? Arrays.asList(values) : null);
    }
    
    public FormDisplayField(String label, String fieldId, List<String> values) {
        this.label = label;
        this.fieldId = fieldId;
        this.values = values != null ? new ArrayList<>(values) : values;
    }
    
    public FieldElementType getElementType() {
        return elementType;
    }

    public void setElementType(FieldElementType elementType) {
        this.elementType = elementType;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getId() {
        return fieldId;
    }

    @Override
    public void setId(String fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public String getValue() {
        return values.size() > 0 ? values.get(0) : null;
    }
    
    public String getValue(int index) {
        return this.getValues().get(index);
    }
    
    @Override
    public void setValue(String value) {
        this.values.clear();
        this.values.add(value);
    }
    
    public void addValue(String value) {
        this.values.add(value);
    }
    
    public List<String> getValues() {
        return this.values;
    }
    
    public void setValues(List<String> values) {
        this.values = values;
    }

    public ValueDisplayMode getValueDisplayMode() {
        return valueDisplayMode;
    }

    public void setValueDisplayMode(ValueDisplayMode valueDisplayMode) {
        this.valueDisplayMode = valueDisplayMode;
    }

    @JsonAnyGetter
    public Map<String, Object> any() {
        return this.optionalProperties;
    }

    @JsonAnySetter
    public void put(String name, Object value) {
        this.optionalProperties.put(name, value);
    }    
    
}
