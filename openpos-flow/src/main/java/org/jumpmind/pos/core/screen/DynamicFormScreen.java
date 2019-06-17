package org.jumpmind.pos.core.screen;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.template.SellTemplate;

public class DynamicFormScreen extends Screen implements IHasForm {

    private static final long serialVersionUID = 1L;

    private Form form = new Form();
    
    private ActionItem submitButton;
    
    private List<String> alternateSubmitActions = new ArrayList<String>();

    public DynamicFormScreen() {
        setScreenType(ScreenType.DynamicForm);
        setTemplate(new SellTemplate());
        submitButton = new ActionItem("Next", "Next", IconType.Forward);
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
    
    @Deprecated
    public void setSubmitAction(String submitAction) {
        if(submitButton == null) {
            submitButton = new ActionItem();
        }
        submitButton.setAction(submitAction);
    }
    @Deprecated
    public String getSubmitAction() {
        return submitButton == null ? null :submitButton.getAction();
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
    
    public void setAlternateSubmitActions(String ...alternateActions) {
        this.setAlternateSubmitActions(Arrays.asList(alternateActions));
    }

    @Deprecated
	public String getSubmitButtonText() {
		return submitButton == null ? null: submitButton.getTitle();
	}

    @Deprecated
	public void setSubmitButtonText(String submitButtonText) {
        if(submitButton == null) {
            submitButton = new ActionItem();
        }
		submitButton.setTitle(submitButtonText);
	}

    @Deprecated
	public String getSubmitIcon() {
		return submitButton == null ? null: submitButton.getIcon();
	}

    @Deprecated
	public void setSubmitIcon(String submitIcon) {
        if(submitButton == null) {
            submitButton = new ActionItem();
        }
		this.submitButton.setIcon(submitIcon);
	}

	public ActionItem getSubmitButton() {
		return submitButton;
	}

	public void setSubmitButton(ActionItem submitButton) {
		this.submitButton = submitButton;
	}
    
}
