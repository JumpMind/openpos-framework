package org.jumpmind.pos.core.audio;

import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.jumpmind.pos.core.content.ContentProviderService;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.core.service.ClientLocaleService;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Scope("device")
public class AudioService implements IAudioService {
    @Value("${openpos.audio.enabled:true}")
    protected boolean enableAudio;

    @In(scope = ScopeType.Device)
    protected IStateManager stateManager;

    @Autowired
    protected ContentProviderService contentProviderService;

    @Autowired
    protected IMessageService messageService;
    
    protected Locale selectedLocale;

    @Override
    public void play(String sound) {
        play(AudioRequest.builder().sound(sound).build());
    }

    @Override
    public void play(AudioRequest request) {
        if (!enableAudio) {
            log.warn("Audio is disabled on device '{}', so the sound '{}' will not be played", stateManager.getDeviceId(), request.getSound());
            return;
        }
        
        String audioKey = AudioUtil.getKey(request.getSound());
        if (request.getLocalized() != null && request.getLocalized().booleanValue() && selectedLocale != null) {
        	audioKey = audioKey + "/" + selectedLocale.getLanguage() + "_" + selectedLocale.getCountry();
        }
        String url = contentProviderService.resolveContent(stateManager.getDeviceId(), audioKey);

        if (StringUtils.isBlank(url)) {
            log.warn("Unable to find sound '{}' for device '{}'", request.getSound(), stateManager.getDeviceId());
            return;
        }

        AudioRequest requestCopy = (AudioRequest)request.clone();
        requestCopy.setUrl(url);
        AudioMessage message = new AudioMessage(requestCopy);


        log.info(String.format("Sending sound '%s' to be played on device '%s'", url, stateManager.getDeviceId()), message);
        messageService.sendMessage(stateManager.getAppId(), stateManager.getDeviceId(), new AudioMessage(requestCopy));
    }
    
    public void setLocale(Locale locale) {
    	selectedLocale = locale;
    }
}
