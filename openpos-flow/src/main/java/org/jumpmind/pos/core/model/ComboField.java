package org.jumpmind.pos.core.model;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ComboField extends FormField implements IDynamicListField {
    
    private static final long serialVersionUID = 1L;

    private List<String> values;
    
    private String valueChangedAction;
    private boolean dynamicListEnabled = true;

    public ComboField() {
        setInputType(FieldInputType.ComboBox);
        setElementType(FieldElementType.Input);
    }
    
    public ComboField(String fieldId, String placeholder) {
        super(fieldId, placeholder);
        setInputType(FieldInputType.ComboBox);
        setElementType(FieldElementType.Input);
    }

    public ComboField(String fieldId, String label, String placeholder) {
        super(fieldId, label, placeholder);
        setInputType(FieldInputType.ComboBox);
        setElementType(FieldElementType.Input);

    }

    public ComboField(String fieldId, String label, String placeholder, List<String> values) {
        super(fieldId, label, placeholder);
        setInputType(FieldInputType.ComboBox);
        setElementType(FieldElementType.Input);
        this.values = values;
    }
    
    @Override
    public List<String> searchValues(String searchTerm, Integer sizeLimit) {
        if (searchTerm != null) {
            return values != null ? 
                    values.stream().filter(v -> v.toLowerCase().contains(searchTerm.toLowerCase()))
                        .limit(sizeLimit != null && sizeLimit >= 0 ? sizeLimit : values.size())
                        .collect(Collectors.toList()) 
                    : null;
        } else {
            return this.getValues();
        }
    }
    
    /**
     * Provides the optional list of values to send over in the Screen JSON.
     *  
     * In the JSON generated, the values property will either be null when dynamicListEnabled=true or 
     * will be the actual list of values. If dynamicListEnabled=true, then values can
     * be fetched via callback from the client to server.
     */
    @JsonGetter("values")
    public List<String> getValuesDynamic() {
        // Values will be fetched dynamically if dynamicListEnabled = true, 
        // so don't return the values in Json
        return this.isDynamicListEnabled() ? null : values;
    }

    @JsonIgnore
    public List<String> getValues() {
        return values;
    }

    @JsonSetter("values")
    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getValueChangedAction() {
        return valueChangedAction;
    }

    /**
     * When this value is set, the client will call back upon the selected value changing with an action whose name is the same 
     * as the one given
     * @param valueChangedAction The name of an action to generate when the Combo box selected value changes.
     */
    public void setValueChangedAction(String valueChangedAction) {
        this.valueChangedAction = valueChangedAction;
    }
    
    @Override
    public void setDynamicListEnabled(boolean enabled) {
        this.dynamicListEnabled = enabled;
    }
    
    @Override
    public boolean isDynamicListEnabled() { 
        return this.dynamicListEnabled;
    }
    
}
