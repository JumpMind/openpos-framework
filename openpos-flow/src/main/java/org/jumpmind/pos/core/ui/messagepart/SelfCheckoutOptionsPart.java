package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.ui.ActionItem;

public class SelfCheckoutOptionsPart implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ActionItem> options;

    private List<ActionItem> additionalButtons;

    private List<ActionItem> linkButtons;

    public List<ActionItem> getOptions() {
        return options;
    }

    public void setOptions(List<ActionItem> options) {
        this.options = options;
    }

    public void addOption(ActionItem option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
    }

    public List<ActionItem> getAdditionalButtons() {
        return additionalButtons;
    }

    public void setAdditionalButtons(List<ActionItem> additionalButtons) {
        this.additionalButtons = additionalButtons;
    }

    public List<ActionItem> getLinkButtons() {
        return linkButtons;
    }

    public void setLinkButtons(List<ActionItem> linkButtons) {
        this.linkButtons = linkButtons;
    }

}
