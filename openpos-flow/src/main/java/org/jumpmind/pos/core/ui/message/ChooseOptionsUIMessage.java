package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.FormOptionItem;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class ChooseOptionsUIMessage extends UIMessage {

    private List<FormOptionItem> options = new ArrayList<FormOptionItem>();
    private String instructions;

    public ChooseOptionsUIMessage() {
        setScreenType(UIMessageType.CHOOSE_OPTIONS);
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
