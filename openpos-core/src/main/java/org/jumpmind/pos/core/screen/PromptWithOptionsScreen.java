package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PromptWithOptionsScreen extends PromptScreen {
    private static final long serialVersionUID = 1L;

    private List<OptionItem> options;

    public PromptWithOptionsScreen() {
        this(new ArrayList<>());
    }

    public PromptWithOptionsScreen(List<OptionItem> options) {
        super();
        this.options = options;
        setType(ScreenType.PromptWithOptions);
    }

    public void addOption(OptionItem option) {
        this.getOptions().add(option);
    }

    public List<OptionItem> getOptions() {
        return options;
    }

    public void setOptions(List<OptionItem> options) {
        this.options = options;
    }

    public void removeOptionWithAction(String name) {
        for (Iterator<OptionItem> iterator = options.iterator(); iterator.hasNext();) {
            OptionItem optionItem = (OptionItem) iterator.next();
            if (optionItem.getAction().equals(name)) {
                iterator.remove();
            }
        }
    }

}
