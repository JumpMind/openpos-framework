package org.jumpmind.pos.core.ui.message;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormOptionItem;
import org.jumpmind.pos.core.screen.IHasForm;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class ChooseOptionsUIMesage extends UIMessage implements IHasForm {

    private List<FormOptionItem> options = new ArrayList<FormOptionItem>();
    private Form submittedForm;
    private String instructions;

    public ChooseOptionsUIMesage() {
        setScreenType(UIMessageType.CHOOSE_OPTIONS);
    }

    @Override
    public void setForm(Form form) {
        submittedForm = form;
    }

    @Override
    public Form getForm() {
        return submittedForm;
    }

    public List<FormOptionItem> getOptions() {
        return options;
    }

    public void setOptions(List<FormOptionItem> options) {
        this.options = options;
    }

    public void addOption(FormOptionItem form) {
        if(options == null) {
            options = new ArrayList<>();
        }

        options.add(form);
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
