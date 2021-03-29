package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IHasForm;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@Data
public class LoyaltyCustomerFormUIMessage extends UIMessage implements IHasForm {

    private Form form = new Form();
    private List<UIMembership> memberships;
    private String membershipsLabel;
    private String noMembershipsLabel;

    private ActionItem addPhone;
    private ActionItem removePhone;
    private ActionItem clearPhone;
    private ActionItem addEmail;
    private ActionItem removeEmail;
    private ActionItem clearEmail;
    private ActionItem submitButton;

    private String profileIcon;
    private String loyaltyNumberIcon;
    private String phoneIcon;
    private String emailIcon;
    private String locationIcon;
    private String addIcon;
    private String removeIcon;
    private String checkMarkIcon;

    private String instructions;

    private List<String> alternateSubmitActions = new ArrayList<String>();

    private String imageUrl;

    public LoyaltyCustomerFormUIMessage() {
        setScreenType(UIMessageType.LOYALTY_CUSTOMER_DIALOG);
    }

}
