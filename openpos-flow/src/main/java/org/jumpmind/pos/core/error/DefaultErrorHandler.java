package org.jumpmind.pos.core.error;

import org.jumpmind.pos.core.audio.AudioConfig;
import org.jumpmind.pos.core.audio.IAudioService;
import org.jumpmind.pos.core.flow.ApplicationState;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.ui.Toast;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.event.ErrorEvent;
import org.jumpmind.pos.util.event.EventPublisher;
import org.jumpmind.pos.util.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultErrorHandler implements IErrorHandler {
    @Autowired
    private AudioConfig audioConfig;

    @Autowired
    private IIncidentService incidentService;

    @Autowired
    private IMessageService messageService;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public void handleError(IStateManager stateManager, Throwable throwable) {
        Message message = incidentService.createIncident(throwable, new IncidentContext(stateManager.getDeviceId()));
        if (message instanceof UIMessage) {
            stateManager.showScreen((UIMessage) message);
        } else if (message instanceof Toast) {
            stateManager.showToast((Toast) message);
        } else {
            messageService.sendMessage(stateManager.getDeviceId(), message);
        }

        ApplicationState applicationState = stateManager.getApplicationState();
        IAudioService audioService = applicationState.getScopeValue("audioService");

        if (audioService != null && audioConfig.getSystemErrorSounds() != null) {
            audioConfig.getSystemErrorSounds().forEach(audioService::play);
        }

        eventPublisher.publish(new ErrorEvent(
                stateManager.getAppId(), stateManager.getDeviceId(), stateManager.getPairedDeviceId(), throwable
        ));
    }
}
