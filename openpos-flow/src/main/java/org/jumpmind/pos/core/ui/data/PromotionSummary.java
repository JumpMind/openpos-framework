package org.jumpmind.pos.core.ui.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.model.DisplayProperty;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PromotionSummary implements Serializable {

    private String name;
    private DisplayProperty promotionId;
    private DisplayProperty startTime;
    private DisplayProperty endTime;
    private DisplayProperty usedTime;
    private DisplayProperty autoApply;
    private DisplayProperty promptUser;
    private String icon;
    private List<ActionItem> actions;

}
