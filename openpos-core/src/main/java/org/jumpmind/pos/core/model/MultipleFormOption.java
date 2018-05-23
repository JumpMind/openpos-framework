package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class MultipleFormOption implements Serializable{

	private static final long serialVersionUID = 1L;
	private Form form = new Form();
	private String icon;
	private String name;
	private String submitAction;
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
	public String getSubmitAction() {
		return submitAction;
	}
	public void setSubmitAction(String submitAction) {
		this.submitAction = submitAction;
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
}
