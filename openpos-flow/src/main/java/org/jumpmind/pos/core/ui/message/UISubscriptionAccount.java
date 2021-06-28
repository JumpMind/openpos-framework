package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.List;

@Data
public class UISubscriptionAccount implements Serializable {
    String iconImageUrl;
    String iconText;
    String subscriptionAccountCopy;
    String subscriptionAccountListTitle;
    List<UIEnrollmentItem> subscriptionAccountListItems;
    ActionItem signupActionItem;
}
