package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.AssignKeyBindings;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.ArrayList;
import java.util.List;

@Data
@AssignKeyBindings
public class CustomerDetailsUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String title;

    private ActionItem backButton;
    private ActionItem unlinkButton;
    private ActionItem editButton;
    private ActionItem doneButton;
    private List<ActionItem> additionalActions;

    private UICustomerDetailsItem customer;

    private Boolean membershipEnabled;
    private Boolean membershipPointsEnabled;
    private Boolean rewardTabEnabled;
    private Boolean rewardHistoryTabEnabled;

    private String appliedLabel;
    private String membershipLabel;
    private String contactLabel;
    private String noPromotionsText;
    private String rewardsLabel;
    private String expiresLabel;
    private String rewardHistoryLabel;
    private String expiredLabel;
    private String loyaltyProgramNameLabel;
    private String pointsLabel;
    private String redeemedLabel;
    private String noMembershipsFoundLabel;
    private String profileIcon;
    private String membershipCardIcon;
    private String membershipPointsIcon;
    private String emailIcon;
    private String phoneIcon;
    private String loyaltyIcon;
    private String loyaltyNumberIcon;
    private String locationIcon;
    private String memberIcon;
    private String nonMemberIcon;
    private String expiredIcon;
    private String appliedIcon;

    public CustomerDetailsUIMessage() {
        setScreenType(UIMessageType.CUSTOMER_DETAILS_DIALOG);
    }

    public void addAdditionalAction(ActionItem action) {
        if (additionalActions == null) {
            additionalActions = new ArrayList<>();
        }
        additionalActions.add(action);
    }
}
