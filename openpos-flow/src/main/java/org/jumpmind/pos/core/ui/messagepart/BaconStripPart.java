package org.jumpmind.pos.core.ui.messagepart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IHasBackButton;
import org.jumpmind.pos.core.ui.IconType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaconStripPart implements IHasBackButton, Serializable {
    private static final long serialVersionUID = 1L;

    private boolean hasCurrentUser;
    private String operatorLine1;
    private String operatorLine2;
    private String headerText;
    private String logo;
    private List<ActionItem> actions;
    private String icon;
    private List<ActionItem> operatorMenu;
    private String operatorIcon;

    @Override
    public void setBackButton(ActionItem button) {
        if (button != null) {
            if (actions == null) {
                actions = new ArrayList<>();
            }
            if (button.getIcon() == null) {
                button.setIcon(IconType.Back);
            }
            actions.add(button);
        }
    }
}
