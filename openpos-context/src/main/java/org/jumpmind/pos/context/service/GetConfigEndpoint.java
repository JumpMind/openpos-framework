package org.jumpmind.pos.context.service;

import java.util.Date;

import org.jumpmind.pos.context.model.ConfigModel;
import org.jumpmind.pos.context.model.ContextRepository;
import org.jumpmind.pos.context.model.Node;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Transactional
public class GetConfigEndpoint {
    
    @Autowired
    private ContextRepository contextRepository;
    @Autowired
    ContextService contextService;
    
    @Endpoint("/getConfig")
    public ConfigResult getConfig(
            @RequestParam(value="nodeId", defaultValue="*") String nodeId,
            @RequestParam(value="configName", defaultValue="") String configName) {    
        
        // TODO get the date from the TimeService.
        
        ConfigResult result = new ConfigResult();
        
        NodeResult nodeResult = contextService.getNode(nodeId);
        if (!nodeResult.isSuccess()) {
            result.setResultStatus(nodeResult.getResultStatus());
            result.setResultMessage(nodeResult.getResultMessage());
            return result;
        }
        
        Node node = nodeResult.getNode();
        
        ConfigModel model = contextRepository.findConfigValue(new Date(), node.getTags(), configName);
        if (model != null) {
            result.setResultStatus(ServiceResult.RESULT_SUCCESS);
            result.setConfigName(configName);
            result.setConfigValue(model.getConfigValue());
            result.setConfig(model);
        } else {
            result.setResultStatus(ServiceResult.RESULT_NOT_FOUND);
            result.setResultMessage("No config found for " + configName);
        }
        
        return result;
        
    }

}
