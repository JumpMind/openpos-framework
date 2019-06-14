package org.jumpmind.pos.core.model;


public class CounterField extends FormField {
    
    private static final long serialVersionUID = 1L;
    
    private Integer minValue;
    private Integer maxValue;
    
    public CounterField() {
        setInputType(FieldInputType.Counter);
        setElementType(FieldElementType.Input);
    }
    
    public CounterField(String id, String label) {
        this();
        this.setId(id);
        this.setLabel(label);
    }
    
    public CounterField(String id, String label, boolean required) {
        this(id, label);
        this.setRequired(required);
    }
    
    public CounterField(String id, String label, boolean required, Integer minValue) {
        this(id, label, required);
        this.setMinValue(minValue);
    }
    
    public CounterField(String id, String label, boolean required, Integer minValue, Integer maxValue) {
        this(id, label, required, minValue);
        this.setMaxValue(maxValue);
    }
    
    public CounterField(String id, String label, boolean required, Integer minValue, Integer maxValue, String value) {
        this(id, label, required, minValue, maxValue);
        this.setValue(value);
    }

    public Integer getMinValue() {
        return minValue;
    }

    public void setMinValue(Integer minValue) {
        this.minValue = minValue;
    }

    public Integer getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(Integer maxValue) {
        this.maxValue = maxValue;
    }
    
}