package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.List;

@Data
public class UIEnrollmentItem implements Serializable {
    UIEnrollmentItemProperty title;
    String icon;
    List<UIEnrollmentItemProperty> details;
    List<ActionItem> actionItemList;
}
