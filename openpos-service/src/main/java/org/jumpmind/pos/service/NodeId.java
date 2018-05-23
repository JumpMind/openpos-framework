package org.jumpmind.pos.service;

import org.apache.commons.lang3.StringUtils;

public class NodeId {
    
    private String nodeId;
    private String locationId;
    private String workstationId;

    public NodeId(String nodeId) {
        if (!StringUtils.isEmpty(nodeId)) {
            String[] parts = nodeId.split("-");
            if (parts.length != 2) {
                throw new PosServerException("nodeId is not the right format. "
                        + "Should be locationId-workstationId, but was '" + nodeId + "'");    
            }
            this.nodeId = nodeId;
            this.locationId = parts[0];
            this.workstationId = parts[1];
            if (StringUtils.isEmpty(this.locationId) || StringUtils.isEmpty(this.workstationId)) {
                throw new PosServerException("nodeId is not the right format. "
                        + "Should be locationId-workstationId, but was '" + nodeId + "'");                
            }
            
        } else {
            throw new PosServerException("nodeId cannot be empty.");
        }
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getLocationId() {
        return locationId;
    }

    public String getWorkstationId() {
        return workstationId;
    }
    
}
