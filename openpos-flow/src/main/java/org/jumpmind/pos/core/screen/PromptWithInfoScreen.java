package org.jumpmind.pos.core.screen;

import java.util.List;

import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.model.FormDisplayField;


public class PromptWithInfoScreen extends PromptScreen {
    private static final long serialVersionUID = 1L;

    public enum PromptPosition {
        Bottom,
        Top
    }
    
    private PromptPosition promptPosition = PromptPosition.Bottom;
    private Form form;

    public PromptWithInfoScreen() {
        this(null);
    }

    public PromptWithInfoScreen(List<FormDisplayField> infoFields) {
        this.form = new Form();
        if (infoFields != null) {
            infoFields.stream().forEachOrdered(f -> this.form.addFormElement(f));
        }
        this.setScreenType(ScreenType.PromptWithInfo);
    }
    
    public void addInfoField(FormDisplayField infoField) {
        this.form.addFormElement(infoField);
    }

    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public PromptPosition getPromptPosition() {
        return promptPosition;
    }

    public void setPromptPosition(PromptPosition promptPosition) {
        this.promptPosition = promptPosition;
    }
    

}
