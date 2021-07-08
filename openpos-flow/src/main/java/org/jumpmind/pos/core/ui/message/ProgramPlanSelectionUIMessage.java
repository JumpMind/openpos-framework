package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.List;

@Data
public class ProgramPlanSelectionUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    List<UISubscriptionPlan> subscriptionPlans;

    public ProgramPlanSelectionUIMessage() {
        setScreenType(UIMessageType.PROGRAM_PLANS_SELECT_DIALOG);
    }
}
