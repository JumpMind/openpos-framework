package org.jumpmind.pos.translate.service;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerFactory;
import org.jumpmind.pos.translate.ILegacyStartupService;
import org.jumpmind.pos.translate.ITranslationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping(value = "register")
public class RegisterService {

    @Autowired
    protected ILegacyStartupService startupService;
    
    @Autowired
    protected IStateManagerFactory stateManagerFactory;
    
    @Autowired
    protected ITranslationManager translationManager;

    @RequestMapping(method = RequestMethod.GET, value = "restart/node/{nodeId}")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public void restart(@PathVariable String nodeId) {
        // TODO: enhance to check success/fail and return status in response.
        if (this.startupService.restart(nodeId)) {
            // TODO: Will probably need to also pass appId through web service call
            IStateManager stateManager = stateManagerFactory.retrieve("pos", nodeId);
            // End existing conversation for the node and reset back to initial state
            stateManager.endConversation();
            // Restart action is handled by the Translator state and will reset subscriber on the remote VM
            stateManager.doAction("Restart");
        }
    }
}
