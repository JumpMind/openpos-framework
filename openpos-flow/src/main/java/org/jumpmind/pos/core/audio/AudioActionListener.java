package org.jumpmind.pos.core.audio;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.IStateManagerContainer;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IActionListener;
import org.jumpmind.pos.server.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    @Autowired()
    AudioConfig audioConfig;

    @Override
    public Collection<String> getRegisteredTypes() {
        return Collections.singletonList("Audio");
    }

    @Override
    public void actionOccurred(String deviceId, Action action) {
        IStateManager stateManager = stateManagerContainer.retrieve(deviceId);
        audioConfig = audioConfig != null ? audioConfig : new AudioConfig();

        if ("GetConfig".equals(action.getName())) {
            onGetConfig(stateManager);
        } else if ("Preload".equals(action.getName())) {
            onPreload(stateManager);
        } else if ("Play".equals(action.getName())) {
            onPlay(stateManager, action);
        }
    }

    public void onGetConfig(IStateManager stateManager) {
        AudioConfigMessage message = AudioUtil.getInteractionMessageFromConfig(stateManager, audioConfig);
        log.debug("Sending audio configuration", message);
        this.messageService.sendMessage(stateManager.getDeviceId(), message);
    }

    public void onPreload(IStateManager stateManager) {
        log.debug("Preloading sounds");
        List<String> urls = new ArrayList<>();

        if (audioConfig.getEnabled() != null && audioConfig.getEnabled()) {
            urls = AudioUtil.getAllContentUrls(stateManager);
        } else {
            log.debug("Not getting content URLs to preload because audio is disabled");
        }

        AudioPreloadMessage message = AudioPreloadMessage.builder().urls(urls).build();
        this.messageService.sendMessage(stateManager.getDeviceId(), message);
    }

    public void onPlay(IStateManager stateManager, Action action) {
        if (action.getData() == null) {
            log.debug("Skipping audio request because there is no data for the request");
            return;
        }

        IAudioService audioService = stateManager.getApplicationState().getScopeValue("audioService");
        AudioRequest audioRequest = Action.convertActionData(action.getData(), AudioRequest.class);

        if (audioConfig.getEnabled() != null && !audioConfig.getEnabled()) {
            log.info("Not playing sound {} because audio is disabled", audioRequest.getSound());
            return;
        }

        log.info(String.format("Received request to play '%s'", audioRequest.getSound()), audioRequest);
        audioService.play(audioRequest);
    }
}
