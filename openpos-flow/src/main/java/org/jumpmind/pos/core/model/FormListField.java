package org.jumpmind.pos.core.model;

import java.util.List;
import java.util.stream.Collectors;

import org.jumpmind.pos.core.screen.SelectionMode;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class FormListField extends FormField implements IDynamicListField {
    private static final long serialVersionUID = 1L;

    private List<String> values;
    
    private SelectionMode selectionMode = SelectionMode.Single;
    private int[] selectedIndexes = {};
    private int selectedIndex = 0;
    private boolean dynamicListEnabled = true;
    
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
 
    @Override
    public void setDynamicListEnabled(boolean enabled) {
        this.dynamicListEnabled = enabled;
    }
    
    @Override
    public boolean isDynamicListEnabled() { 
        return this.dynamicListEnabled;
    }
    
}
