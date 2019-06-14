package org.jumpmind.pos.core.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class CheckboxField extends FormField {
    private static final long serialVersionUID = 1L;
    
    public enum LabelPosition {
        before,
        after
    }
    
    private LabelPosition labelPosition = LabelPosition.after;
    
    public CheckboxField() {
        setInputType(FieldInputType.Checkbox);
        setElementType(FieldElementType.Input);
    }
    
    public CheckboxField(String id, String label, boolean checked) {
        this();
        this.setId(id);
        this.setLabel(label);
        this.setChecked(checked);
    }
    

    public void setChecked(boolean checked) {
        this.setValue(checked ? "checked" : null);
    }
    
    public boolean isChecked() {
        return getValue() != null;
    }
    
    @JsonIgnore
    public boolean isNotChecked() {
        return ! isChecked();
    }

    public CheckboxField checked() {
        this.setChecked(true);
        return this;
    }
    
    @JsonIgnore
    public CheckboxField notChecked() {
        this.setChecked(false);
        return this;
    }
    
    public LabelPosition getLabelPosition() {
        return labelPosition;
    }

    public void setLabelPosition(LabelPosition labelPosition) {
        this.labelPosition = labelPosition;
    }
    
    public CheckboxField labelPosition(LabelPosition labelPosition) {
        this.setLabelPosition(labelPosition);
        return this;
    }
}
