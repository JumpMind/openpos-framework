package org.jumpmind.pos.core.model;

import java.io.Serializable;

import org.jumpmind.pos.core.ui.ActionItem;

@Deprecated
public class MultipleFormOption implements Serializable {

	private static final long serialVersionUID = 1L;
	private Form form = new Form();
	private String icon;
	private String name;
	private ActionItem submitButton;
	private String instructions;
	
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
    public ActionItem getSubmitButton() {
        return submitButton;
    }
    public void setSubmitButton(ActionItem menuItem) {
        this.submitButton = menuItem;
    }
}
