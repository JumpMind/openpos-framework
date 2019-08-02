package org.jumpmind.pos.core.model;

import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;

public class FormOptionItem implements Serializable {

    private static final long serialVersionUID = 1L;
    private Form form = new Form();
    private String icon;
    private String name;
    private ActionItem optionAction;
    private String instructions;
    private boolean enabled=true;
    
    public FormOptionItem() {}

    public FormOptionItem(String actionID, String label, String iconType) {
    	setOptionAction(new ActionItem(actionID,label));
    	setName(label);
    	setIcon(iconType);
	}

	public Form getForm() {
        return form;
    }
    public void setForm(Form form) {
        this.form = form;
    }
    public String getIcon() {
        return icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getInstructions() {
        return instructions;
    }
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public ActionItem getOptionAction() {
        return optionAction;
    }
    public void setOptionAction(ActionItem optionAction) {
        this.optionAction = optionAction;
    }
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

