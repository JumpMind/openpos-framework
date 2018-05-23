package org.jumpmind.pos.context.service;

import org.jumpmind.pos.context.model.Node;
import org.jumpmind.pos.service.ServiceResult;

public class NodeResult extends ServiceResult {

    private String nodeId;
    private Node node;
    
    public String getNodeId() {
        return nodeId;
    }
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    public Node getNode() {
        return node;
    }
    public void setNode(Node node) {
        this.node = node;
    }
    
}
