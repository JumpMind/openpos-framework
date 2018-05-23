package org.jumpmind.pos.core.model;

import java.util.List;
import java.util.stream.Collectors;

import org.jumpmind.pos.core.screen.SelectionMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class FormListField extends FormField {
    private static final long serialVersionUID = 1L;

    // Use @JsonIgnore annotation since the jackson serializer 
    @JsonIgnore
    private List<String> values;
    
    private SelectionMode selectionMode = SelectionMode.Single;
    private int[] selectedIndexes = {};
    private int selectedIndex = 0;
    
    public FormListField() {
    }
    
    public FormListField(String fieldId, String placeholder) {
        super(fieldId, placeholder);
    }

    public FormListField(String fieldId, String placeholder, List<String> values) {
        super(fieldId, placeholder);
        this.values = values;
    }
    
    public FormListField(String fieldId, String label, String placeholder) {
        super(fieldId, label, placeholder);
    }

    public FormListField(String fieldId, String label, String placeholder, List<String> values) {
        super(fieldId, label, placeholder);
        this.values = values;
    }
    
    public FormListField(String fieldId, String label, FieldElementType elementType, FieldInputType inputType, String placeholder) {
        super(fieldId, label, elementType, inputType, placeholder);
    }

    public FormListField(String fieldId, String label, FieldElementType elementType, FieldInputType inputType, String placeholder, List<String> values) {
        super(fieldId, label, elementType, inputType, placeholder);
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

    public SelectionMode getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public int[] getSelectedIndexes() {
        return selectedIndexes;
    }

    public void setSelectedIndexes(int[] selectedIndexes) {
        this.selectedIndexes = selectedIndexes;
    }
    
    public int getSelectedIndex() {
        return selectedIndex;
    }
        
    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }    
    
}
