package org.jumpmind.pos.core.ui.data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Promotion implements Serializable {
    String icon;
    String promotionId;
    String promotionName;
    BigDecimal priority;
    boolean singleUse = false;
    boolean autoApply = true;
    BigDecimal maxUses;
    boolean vendorFunded;
    String rewardApplicationTypeCode;
    boolean forLoyaltyReward;
    String promotionPrice;
}
