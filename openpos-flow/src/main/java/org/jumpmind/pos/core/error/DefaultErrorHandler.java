package org.jumpmind.pos.core.error;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.ui.Toast;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.service.IIncidentService;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.server.service.IncidentContext;
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
        if(message instanceof UIMessage) {
            stateManager.showScreen((UIMessage) message);
        } else if (message instanceof Toast) {
            stateManager.showToast((Toast)message);
        } else {
            messageService.sendMessage(stateManager.getAppId(), stateManager.getDeviceId(), message);
        }
    }
}
