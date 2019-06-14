package org.jumpmind.pos.core.model;

public class SliderToggleField extends CheckboxField {

    private static final long serialVersionUID = 1L;

    public SliderToggleField() {
        setInputType(FieldInputType.SliderToggle);
        setElementType(FieldElementType.Input);
        this.setRequired(false);
    }

    public SliderToggleField(String id, String label, boolean checked) {
        this();
        this.setId(id);
        this.setLabel(label);
        this.setChecked(checked);
    }

}
