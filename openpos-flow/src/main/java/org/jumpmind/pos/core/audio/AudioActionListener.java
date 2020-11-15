package org.jumpmind.pos.core.audio;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IActionListener;
import org.jumpmind.pos.server.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class AudioActionListener implements IActionListener {
    @Autowired
    IStateManagerContainer stateManagerContainer;

    @Autowired
    IMessageService messageService;

    @Autowired
    AudioConfig audioConfig;

    @Override
    public Collection<String> getRegisteredTypes() {
        return Collections.singletonList("Audio");
    }

    @Override
    public void actionOccured(String appId, String deviceId, Action action) {
        IStateManager stateManager = stateManagerContainer.retrieve(appId, deviceId);

        if ("GetConfig".equals(action.getName())) {
            AudioConfigMessage message = AudioUtil.getInteractionMessageFromConfig(stateManager, audioConfig);
            log.warn("Sending audio configuration", message);
            this.messageService.sendMessage(appId, deviceId, message);
        } else if ("Preload".equals(action.getName())) {
            log.warn("Preloading sounds");
            List<String> contentUrls = AudioUtil.getAllContentUrls(stateManager);
            AudioPreloadMessage message = AudioPreloadMessage.builder().contentUrls(contentUrls).build();
            this.messageService.sendMessage(appId, deviceId, message);
        } else if ("Play".equals(action.getName())) {
            if (action.getData() == null) {
                log.warn("Skipping audio request because there is no data for the request");
                return;
            }

            IAudioService audioService = stateManager.getApplicationState().getScopeValue("audioService");
            AudioRequest audioRequest = Action.convertActionData(action.getData(), AudioRequest.class);

            log.info(String.format("Received request to play '%s'", audioRequest.getSound()), audioRequest);
            audioService.play(audioRequest);
        }
    }
}
