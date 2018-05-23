package org.jumpmind.pos.context.service;

import org.jumpmind.pos.context.model.ContextRepository;
import org.jumpmind.pos.context.model.Node;
import org.jumpmind.pos.service.Endpoint;
import org.jumpmind.pos.service.ServiceResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@Transactional(transactionManager="contextTxManager")
public class GetNodeEndpoint {
    
    @Autowired
    private ContextRepository contextRepository;    
    
    @Endpoint("/node")
    public NodeResult getNode(
            @RequestParam(value="nodeId", defaultValue="*") String nodeId) {
        
        NodeResult result = new NodeResult();
        Node node = contextRepository.findNode(nodeId);
        
        if (node != null) {
            result.setResultStatus(ServiceResult.RESULT_SUCCESS);
            result.setNodeId(node.getNodeId());
            result.setNode(node);
            return result;
        } else {
            result.setResultStatus(ServiceResult.RESULT_NOT_FOUND);
            result.setResultMessage("No node found for nodeId=" + nodeId);
        }
        
        return result;
    }
}
