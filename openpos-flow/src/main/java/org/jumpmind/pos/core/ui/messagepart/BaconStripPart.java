package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IHasBackButton;
import org.jumpmind.pos.core.ui.IconType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BaconStripPart implements IHasBackButton, Serializable {
    private String deviceId;
    private String operatorText;
    private String headerText;
    private String headerIcon;
    private String logo;
    private List<ActionItem> actions;

    @Override
    public void setBackButton(ActionItem button) {
        if(button != null){
            if(actions == null){
                actions = new ArrayList<>();
            }
            if(button.getIcon() == null){
                button.setIcon(IconType.Back);
            }
            actions.add(button);
        }
    }
}
