package org.jumpmind.pos.core.flow.config;


public class SubFlowConfig {

    private String[] returnActionNames;
    private FlowConfig subFlowConfig;
    
    public SubFlowConfig() {
        
    }
    
    public SubFlowConfig(String[] returnActionNames, FlowConfig subFlowConfig) {
        super();
        this.returnActionNames = returnActionNames;
        this.subFlowConfig = subFlowConfig;
    }
    

    public FlowConfig getSubFlowConfig() {
        return subFlowConfig;
    }
    public void setSubFlowConfig(FlowConfig subFlowConfig) {
        this.subFlowConfig = subFlowConfig;
    }

    public String[] getReturnActionNames() {
        return returnActionNames;
    }

    public void setReturnActionNames(String[] returnActionNames) {
        this.returnActionNames = returnActionNames;
    }
    
}
