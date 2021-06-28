package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.List;

@Data
public class UIEnrollmentItem implements Serializable {
    List<UIEnrollmentItemProperty> properties;
    List<ActionItem> actionItemList;
}
