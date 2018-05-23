package org.jumpmind.pos.core.screen;


import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.template.SellTemplate;

public class DynamicFormScreen extends Screen implements IHasForm {

    private static final long serialVersionUID = 1L;

    private Form form = new Form();
    
    private String submitAction = "Next";
    
    private String submitButtonText = "Next";
    
    private List<String> alternateSubmitActions = new ArrayList<String>();

    public DynamicFormScreen() {
        setType(ScreenType.DynamicForm);
        setTemplate(new SellTemplate());
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Form getForm() {
        return form;
    }

    public String getString(String id) {
        return form.getString(id);
    }
    
    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }
    
    public String getSubmitAction() {
        return submitAction;
    }
    
    public void addAlternateSubmitAction(String action) {
        this.alternateSubmitActions.add( action );
    }

    public List<String> getAlternateSubmitActions() {
        return alternateSubmitActions;
    }

    public void setAlternateSubmitActions(List<String> alternateActions) {
        this.alternateSubmitActions = alternateActions;
    }

	public String getSubmitButtonText() {
		return submitButtonText;
	}

	public void setSubmitButtonText(String submitButtonText) {
		this.submitButtonText = submitButtonText;
	}
    
}
