package org.jumpmind.pos.core.ui.messagepart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.server.model.Action;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptButtonRowPart implements Serializable {
    ActionItem primaryButton;
    List<ActionItem> secondaryButtons = new ArrayList<>();
    List<ActionItem> warnButtons = new ArrayList<>();

    public PromptButtonRowPart(ActionItem primaryButton) {
        this.primaryButton = primaryButton;
    }

    public void addSecondaryButton(ActionItem button) {
        this.secondaryButtons.add(button);
    }

    public void addWarnButton(ActionItem button) {
        this.warnButtons.add(button);
    }
}
