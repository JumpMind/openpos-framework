package org.jumpmind.pos.core.ui.message;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltySignupMessage extends Message {
    private static final long serialVersionUID = 1L;

    @JsonProperty("isActiveOnCustomerDisplay")
    private boolean isActiveOnCustomerDisplay;

    private String message;

    @Override
    public String getType() {
        return MessageType.LoyaltySignup;
    }
}
