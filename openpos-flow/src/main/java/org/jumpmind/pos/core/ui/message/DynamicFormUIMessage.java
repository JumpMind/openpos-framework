package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.IHasForm;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class DynamicFormUIMessage extends UIMessage implements IHasForm {

    private Form form = new Form();

    private ActionItem submitButton;

    private List<String> alternateSubmitActions = new ArrayList<String>();

    public DynamicFormUIMessage() {
        setScreenType(UIMessageType.DYNAMIC_FORM);
    }

    @Override
    public void setForm(Form form) {
        this.form = form;
    }

    @Override
    public Form getForm() {
        return form;
    }

    public ActionItem getSubmitButton() {
        return submitButton;
    }

    public void setSubmitButton(ActionItem submitButton) {
        this.submitButton = submitButton;
    }

    public List<String> getAlternateSubmitActions() {
        return alternateSubmitActions;
    }

    public void setAlternateSubmitActions(List<String> alternateSubmitActions) {
        this.alternateSubmitActions = alternateSubmitActions;
    }

    public void addAlternateSubmitAction( String alternatedSubmitAction ) {

    }
}
