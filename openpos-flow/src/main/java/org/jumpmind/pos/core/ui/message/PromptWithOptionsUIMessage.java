package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.OptionItem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PromptWithOptionsUIMessage extends PromptUIMessage {
    private List<OptionItem> options;

    public PromptWithOptionsUIMessage() {
        this(new ArrayList<>());
    }

    public PromptWithOptionsUIMessage(List<OptionItem> options) {
        super();
        this.options = options;
        setScreenType(UIMessageType.PROMPT_WITH_OPTIONS);
    }

    public List<OptionItem> getOptions() {
        return options;
    }

    public void setOptions(List<OptionItem> options) {
        this.options = options;
    }

    public void addOption(OptionItem option) {
        this.options.add(option);
    }

    public void removeOptionByActionName(String name) {
        for (Iterator<OptionItem> iterator = options.iterator(); iterator.hasNext();) {
            OptionItem optionItem = (OptionItem) iterator.next();
            if (optionItem.getAction().equals(name)) {
                iterator.remove();
            }
        }
    }

    public <T extends ActionItem> void setUIOptions(List<T> options) {
        this.setOptions(options != null ?
                options.stream().map(mi -> new OptionItem(mi.getAction(), mi.getTitle(), mi.isEnabled(), mi.getIcon()))
                        .collect(Collectors.toList()) : null);
    }
}
