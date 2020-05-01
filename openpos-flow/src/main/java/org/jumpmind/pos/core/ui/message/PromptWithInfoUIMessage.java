package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormDisplayField;

import java.util.List;

public class PromptWithInfoUIMessage extends PromptUIMessage {

    private static final long serialVersionUID = 1L;

    public enum PromptPosition {
        Bottom,
        Top
    }
    
    private PromptPosition promptPosition = PromptPosition.Bottom;
    private Form infoForm;

    public PromptWithInfoUIMessage() {
        this(null);
    }

    public PromptWithInfoUIMessage(List<FormDisplayField> infoFields) {
        this.infoForm = new Form();
        if (infoFields != null) {
            infoFields.stream().forEachOrdered(f -> this.infoForm.addFormElement(f));
        }
        this.setScreenType(UIMessageType.PROMPT_WITH_INFO);
    }
    
    public void addInfoField(FormDisplayField infoField) {
        this.infoForm.addFormElement(infoField);
    }

    public Form getInfoForm() {
        return infoForm;
    }

    public void setInfoForm(Form infoForm) {
        this.infoForm = infoForm;
    }

    public PromptPosition getPromptPosition() {
        return promptPosition;
    }

    public void setPromptPosition(PromptPosition promptPosition) {
        this.promptPosition = promptPosition;
    }
   
}
