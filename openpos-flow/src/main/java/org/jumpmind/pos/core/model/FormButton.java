package org.jumpmind.pos.core.model;

import java.io.Serializable;

import org.jumpmind.pos.core.ui.ActionItem;

public class FormButton extends ActionItem implements IFormElement, Serializable {
    private static final long serialVersionUID = 1L;
    
    protected FieldElementType elementType = FieldElementType.Button;
    protected String id;
    public String label;
    protected boolean submitButton = false;

    public FormButton() {
    }
    
    public FormButton(String label, String buttonAction) {
        this(label, label, buttonAction);
    }
    
    public FormButton(String label, String icon, String buttonAction, boolean submit) {
        this(label, label, buttonAction);
        this.submitButton = submit;
        this.icon = icon;
    }

    public FormButton(String id, String title, String buttonAction) {
    	super(buttonAction, title);
        this.id = id;
        this.label = title;
    }

    public FieldElementType getElementType() {
        return elementType;
    }

    public void setElementType(FieldElementType elementType) {
        this.elementType = elementType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(boolean submitButton) {
        this.submitButton = submitButton;
    }
    
}
