package org.jumpmind.pos.core.ui.message;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class UICustomerDetailsItem extends SelectableItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private String privacyRestrictedMessage;
    private String name;
    private String loyaltyNumber;
    private BigDecimal loyaltyPoints;
    private String email;
    private String phoneNumber;
    private UIAddress address;
    private String birthDate;
    private List<UIMembership> memberships = new ArrayList<>();
    private List<UILoyaltyReward> rewards = new ArrayList<>();
    private List<UIRewardHistory> rewardHistory = new ArrayList<>();
}
