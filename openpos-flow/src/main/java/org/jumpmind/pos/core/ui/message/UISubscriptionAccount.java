package org.jumpmind.pos.core.ui.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UISubscriptionAccount implements Serializable {
    String iconImageUrl;
    String iconText;
    String copy;
    String listTitle;
    List<UISubscriptionPlan> plans;
    List<UIEnrollmentItem> enrollmentItems;
    ActionItem signupActionItem;
}
