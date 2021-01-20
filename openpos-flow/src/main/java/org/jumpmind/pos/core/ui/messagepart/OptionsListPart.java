package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.flow.ActionHandler;
import org.jumpmind.pos.core.ui.ActionItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionsListPart implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<ActionItem> options;

    private List<ActionItem> additionalButtons;

    private List<ActionItem> linkButtons;

    private ActionItem overflowButton;

    private boolean firstElementFocused;

    public void addOption(ActionItem option) {
        if (this.options == null) {
            this.options = new ArrayList<>();
        }
        this.options.add(option);
    }
}
