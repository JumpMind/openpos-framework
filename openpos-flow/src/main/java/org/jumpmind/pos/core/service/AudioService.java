package org.jumpmind.pos.core.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jumpmind.pos.core.content.ContentProviderService;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.core.ui.message.AudioMessage;
import org.jumpmind.pos.server.service.IMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("device")
public class AudioService implements IAudioService {
    @Value("${openpos.general.enableAudio:true}")
    protected boolean enableAudio;

    @In(scope = ScopeType.Device)
    protected IStateManager stateManager;

    @Autowired
    protected ContentProviderService contentProviderService;

    @Autowired
    protected IMessageService messageService;

    @Override
    public void play(String key) {
        play(key, null);
    }

    @Override
    public void play(String key, AudioOptions options) {
        if (!enableAudio) {
            log.warn("Audio is disabled on device '{}', so the sound '{}' will not be played", stateManager.getDeviceId(), key);
            return;
        }

        String audioKey = "audio/" + key;
        String url = contentProviderService.resolveContent(stateManager.getDeviceId(), audioKey);

        if(StringUtils.isBlank(url)) {
            log.warn("Unable to find sound '{}' for device '{}'", key, stateManager.getDeviceId());
            return;
        }

        AudioMessage message = AudioMessage.builder().url(url).build();

        if (options != null) {
            message.setStartTime(options.getStartTime());
            message.setEndTime(options.getEndTime());
            message.setLoop(options.getLoop());
            message.setPlaybackRate(options.getPlaybackRate());
            message.setAutoplay(options.getAutoplay());
            message.setVolume(options.getVolume());
            message.setDelayTime(options.getDelayTime());
        }

        log.info(String.format("Sending sound '%s' to be played on device '%s'", message.getUrl(), stateManager.getDeviceId()), message);
        messageService.sendMessage(stateManager.getAppId(), stateManager.getDeviceId(), message);
    }
}
