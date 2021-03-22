package org.jumpmind.pos.core.ui.message;

import lombok.Data;

import java.io.Serializable;

@Data
public class UIRewardHistory implements Serializable {
    private static final long serialVersionUID = 1L;

    private String promotionId;
    private String name;
    private String expirationDate;
    private Boolean redeemed;
}
