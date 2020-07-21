package org.jumpmind.pos.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.util.model.Message;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchMessage extends Message {
    private static final String MESSAGE_TYPE = "Fetch";

    private String messageIdToFetch;

    @Override
    public String getType() {
        return MESSAGE_TYPE;
    }

}
