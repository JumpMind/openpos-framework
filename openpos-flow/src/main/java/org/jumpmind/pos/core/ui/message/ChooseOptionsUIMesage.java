package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.FormOptionItem;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

public class ChooseOptionsUIMesage extends UIMessage {

    private List<FormOptionItem> options = new ArrayList<FormOptionItem>();
    private String instructions;

    public ChooseOptionsUIMesage() {
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
