package org.jumpmind.pos.core.model;

import java.util.List;
import java.util.stream.Collectors;

public class ComboField extends FormField {
    
    private static final long serialVersionUID = 1L;

    private List<String> values;
    
    private String valueChangedAction;
    
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
    
    public List<String> getValues() {
        return values;
    }

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
    
}
