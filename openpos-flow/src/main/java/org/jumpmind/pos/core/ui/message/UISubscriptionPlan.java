package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

@Data
@Builder
public class UISubscriptionPlan {
    String subscriptionAccountCopy;
    ActionItem signupActionItem;
}
