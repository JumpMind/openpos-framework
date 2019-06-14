package org.jumpmind.pos.core.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonSetter;

public class RadioField extends FormField {

    private static final long serialVersionUID = 1L;

    private List<List<String>> values;

    private String valueChangedAction;

    private int selectedIndex;

    public RadioField() {
        setInputType(FieldInputType.Radio);
        setElementType(FieldElementType.Input);
    }

    public RadioField(String fieldId, String label) {
        setInputType(FieldInputType.Radio);
        setElementType(FieldElementType.Input);
        setId(fieldId);
        setLabel(label);
        values = new ArrayList<>();
    }

    public RadioField(String fieldId, String label, List<String> values) {
        this(fieldId, label);
        if (values != null) {
            for (String value : values) {
                addValue(value);
            }
        }
    }

    public void addValue(String... values) {
        if (values != null) {
            List<String> list = new ArrayList<>();
            for (String value : values) {
                list.add(value);
            }
            this.values.add(list);
        }
    }

    public void addValue(List<String> value) {
        this.values.add(value);
    }

    public List<List<String>> getValues() {
        return values;
    }

    @JsonSetter("values")
    public void setValues(List<List<String>> values) {
        this.values = values;
    }

    public String getValueChangedAction() {
        return valueChangedAction;
    }

    /**
     * When this value is set, the client will call back upon the selected value
     * changing with an action whose name is the same as the one given
     * 
     * @param valueChangedAction
     *            The name of an action to generate when the Combo box selected
     *            value changes.
     */
    public void setValueChangedAction(String valueChangedAction) {
        this.valueChangedAction = valueChangedAction;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.setValue(String.valueOf(selectedIndex));
        this.selectedIndex = selectedIndex;
    }

}
