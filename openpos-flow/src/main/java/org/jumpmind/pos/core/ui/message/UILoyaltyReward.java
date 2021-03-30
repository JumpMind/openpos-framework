package org.jumpmind.pos.core.ui.message;

import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;
import java.util.Date;

@Data
public class UILoyaltyReward implements Serializable {
    private static final long serialVersionUID = 1L;

    private String promotionId;
    private String name;
    private String expirationDate;
    private String expirationLabel;
    private ActionItem applyButton;
}
