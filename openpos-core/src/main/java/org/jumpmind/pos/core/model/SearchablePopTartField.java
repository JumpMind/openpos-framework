package org.jumpmind.pos.core.model;

import java.util.List;

public class SearchablePopTartField extends ComboField {

	private static final long serialVersionUID = 1L;

    public SearchablePopTartField() {
        super();
        setInputType(FieldInputType.SearchablePopTart);
    }

    public SearchablePopTartField(String fieldId, String label, String placeholder, List<String> values) {
        super(fieldId, label, placeholder, values);
        setInputType(FieldInputType.SearchablePopTart);
    }

    public SearchablePopTartField(String fieldId, String label, String placeholder) {
        super(fieldId, label, placeholder);
        setInputType(FieldInputType.SearchablePopTart);
    }

    public SearchablePopTartField(String fieldId, String placeholder) {
        super(fieldId, placeholder);
        setInputType(FieldInputType.SearchablePopTart);
    }
    
    public void setInstructions( String instructions ) {
    	this.put("instructions", instructions);
    }

}
