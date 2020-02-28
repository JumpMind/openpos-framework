package org.jumpmind.pos.core.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleSignOnMessage extends Message {
    private String providerName;
    private ActionItem responseAction;
    @Override
    public String getType() {
        return MessageType.SingleSignOnRequest;
    }
}
