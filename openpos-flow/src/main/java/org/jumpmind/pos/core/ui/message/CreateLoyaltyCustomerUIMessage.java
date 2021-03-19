package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.model.Form;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IHasForm;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@Data
public class CreateLoyaltyCustomerUIMessage extends UIMessage implements IHasForm {

    private Form form = new Form();

    private ActionItem addPhone;
    private ActionItem removePhone;
    private ActionItem addEmail;
    private ActionItem removeEmail;
    private ActionItem submitButton;

    private String instructions;    // Does not show up in create-loyalty-customer-form-dialog component

    private List<String> alternateSubmitActions = new ArrayList<String>();

    private String imageUrl;        // Does not show up in create-loyalty-customer-form-dialog component

    public CreateLoyaltyCustomerUIMessage() {
        setScreenType(UIMessageType.CREATE_LOYALTY_CUSTOMER);
    }

}
