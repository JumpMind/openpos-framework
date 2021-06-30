package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.List;

@Data
@AssignKeyBindings
public class CustomerMembershipsUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String title;
    private UICustomerSubscriptionsItem customer;
    List<UISubscriptionAccount> subscriptionAccounts;
    private ActionItem backButton;


    // Icons
    private String profileIcon;
    private String memberIcon;
    private String nonMemberIcon;

    public CustomerMembershipsUIMessage() {
        setScreenType(UIMessageType.MEMBERSHIP_DETAILS_DIALOG);
    }
}
