package org.jumpmind.pos.server.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SessionContext {
    String sessionId;
    boolean isConnected;
    String deviceToken;
}
