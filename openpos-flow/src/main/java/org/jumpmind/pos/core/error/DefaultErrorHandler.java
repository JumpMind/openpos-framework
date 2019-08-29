package org.jumpmind.pos.core.error;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultErrorHandler implements IErrorHandler {

    @Autowired
    private IIncidentService incidentService;

    @Autowired
    private IMessageService messageService;

    @Override
    public void handleError(IStateManager stateManager, Throwable throwable) {
        Message message = incidentService.createIncident(throwable, new IncidentContext(stateManager.getDeviceId()));

        if( message != null ) {
            messageService.sendMessage(stateManager.getAppId(), stateManager.getDeviceId(), message);
        }
    }
}
