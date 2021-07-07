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
    String subscriptionAccountCopy;
    String subscriptionAccountListTitle;
    List<UISubscriptionPlan> subscriptionPlans;
    List<UIEnrollmentItem> subscriptionAccountListItems;
    ActionItem signupActionItem;
}
