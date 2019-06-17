package org.jumpmind.pos.core.model;

public class FormSeparator implements IFormElement {

    String id = "separator";
    
    private FieldElementType elementType = FieldElementType.Separator;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
    
    public FieldElementType getElementType() {
        return elementType;
    }    

}
