package org.jumpmind.pos.core.ui.message;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.MultipleFormOption;
import org.jumpmind.pos.core.screen.IHasForm;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class MultipleFormUIMesage extends UIMessage implements IHasForm {

    private List<MultipleFormOption> forms = new ArrayList<MultipleFormOption>();
    private Form submittedForm;

    public MultipleFormUIMesage() {
        setScreenType(UIMessageType.MULTIPLE_FORM);
    }

    @Override
    public void setForm(Form form) {
        submittedForm = form;
    }

    @Override
    public Form getForm() {
        return submittedForm;
    }

    public List<MultipleFormOption> getForms() {
        return forms;
    }

    public void setForms(List<MultipleFormOption> forms) {
        this.forms = forms;
    }

    public void addForm(MultipleFormOption form) {
        if(forms == null) {
            forms = new ArrayList<>();
        }

        forms.add(form);
    }
}
