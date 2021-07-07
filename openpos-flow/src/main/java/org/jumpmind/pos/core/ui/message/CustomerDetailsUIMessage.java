package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;

@Data
public class CustomerDetailsUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    private String title;

    private ActionItem unlinkButton;
    private ActionItem editButton;
    private ActionItem doneButton;

    private UICustomerDetailsItem customer;

    private Boolean membershipEnabled;
    private Boolean membershipPointsEnabled;
    private Boolean rewardTabEnabled;
    private Boolean rewardHistoryTabEnabled;
    private Boolean itemHistoryEnabled;

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
    private String checkMarkIcon;
    private String expiredIcon;
    private String applyIcon;
    private String birthDateIcon;

    private String itemHistoryLabel;
    private String itemsHistoryDataProviderKey;

    private UICustomerItemHistoryFilter itemHistoryFilter;

    public CustomerDetailsUIMessage() {
        setScreenType(UIMessageType.CUSTOMER_DETAILS_DIALOG);
    }
}
