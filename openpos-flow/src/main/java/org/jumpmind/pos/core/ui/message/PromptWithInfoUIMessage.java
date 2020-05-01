package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.model.*;

import java.util.ArrayList;
import java.util.List;

public class PromptWithInfoUIMessage extends PromptUIMessage {

    private static final long serialVersionUID = 1L;

    public enum PromptPosition {
        Bottom,
        Top
    }

    private PromptPosition promptPosition = PromptPosition.Bottom;
    private List<DisplayProperty> info;

    public PromptWithInfoUIMessage() {
        this(new ArrayList<>());
    }

    public PromptWithInfoUIMessage(List<DisplayProperty> info) {
        this.info = info;
        this.setScreenType(UIMessageType.PROMPT_WITH_INFO);
    }

    public void addInfoField(String label, String value, String valueFormatter) {
        if(this.info == null) {
            this.info = new ArrayList<>();
        }
        this.info.add(new DisplayProperty(label, value, valueFormatter));
    }

    public List<DisplayProperty> getInfo() {
        return info;
    }

    public void setInfo(List<DisplayProperty> info) {
        this.info = info;
    }

    public PromptPosition getPromptPosition() {
        return promptPosition;
    }

    public void setPromptPosition(PromptPosition promptPosition) {
        this.promptPosition = promptPosition;
    }
   
}
