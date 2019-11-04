package org.jumpmind.pos.management;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A response that looks like an OpenPOS server personalization response, but
 * includes an extra flag that indicates that the response is coming from an
 * OpenPOS Management Server.  The client can use this information to alter its
 * behavior for negotiating a connection. 
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImpersonalizationResponse {
    private boolean openposManagementServer = true;
    private String devicePattern;
}
