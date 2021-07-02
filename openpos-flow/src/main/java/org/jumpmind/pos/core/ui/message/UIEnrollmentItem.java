package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class UIEnrollmentItem implements Serializable {
    UIEnrollmentItemProperty title;
    List<UIEnrollmentItemProperty> details;
    List<ActionItem> actionItemList;
}
