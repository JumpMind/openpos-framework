package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.List;

@Data
@AssignKeyBindings
public class CustomerMembershipPlanDetailsUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private UIEnrollmentItem enrollmentItem;
    private UISubscriptionPlan plan;
    private ActionItem doneButton;

    public CustomerMembershipPlanDetailsUIMessage() {
        setScreenType(UIMessageType.MEMBERSHIP_PLAN_DETAILS_DIALOG);
    }
}
