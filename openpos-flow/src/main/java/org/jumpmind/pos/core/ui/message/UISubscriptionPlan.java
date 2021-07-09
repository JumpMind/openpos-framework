package org.jumpmind.pos.core.ui.message;

import lombok.Builder;
import lombok.Data;
import org.jumpmind.pos.core.ui.ActionItem;

import java.io.Serializable;

@Data
@Builder
public class UISubscriptionPlan implements Serializable {
    String copy;
    ActionItem signupActionItem;
}
