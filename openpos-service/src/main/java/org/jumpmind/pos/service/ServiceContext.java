package org.jumpmind.pos.service;


public class ServiceContext {

    private String nodeId;
    private String locale;
    
    public String getNodeId() {
        return nodeId;
    }
    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }
    public String getLocale() {
        return locale;
    }
    public void setLocale(String locale) {
        this.locale = locale;
    }
    
    public NodeId getNodeIdModel() {
        return new NodeId(nodeId);
    }
    
}
