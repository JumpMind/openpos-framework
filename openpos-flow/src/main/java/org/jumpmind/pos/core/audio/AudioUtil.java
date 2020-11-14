package org.jumpmind.pos.core.audio;

import org.jumpmind.pos.core.content.ContentProviderService;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.util.ObjectUtils;

public final class AudioUtil {
    public static final String AUDIO_CONTENT_ROOT = "audio/";

    public static AudioConfigMessage getInteractionMessageFromConfig(IStateManager stateManager, AudioConfig config) {
        AudioInteractionSet interactionsCopy = ObjectUtils.deepClone(config.getInteractions());
        AudioConfigMessage message = AudioConfigMessage.builder()
                .enabled(config.getEnabled())
                .interactions(interactionsCopy)
                .build();

        if (message.getInteractions() != null && message.getInteractions().getMouse() != null) {
            updateSoundWithProviderUrl(stateManager, message.getInteractions().getMouse().getClickIn());
            updateSoundWithProviderUrl(stateManager, message.getInteractions().getMouse().getClickOut());
        }

        if (message.getInteractions() != null && message.getInteractions().getDialog() != null) {
            updateSoundWithProviderUrl(stateManager, message.getInteractions().getDialog().getOpening());
            updateSoundWithProviderUrl(stateManager, message.getInteractions().getDialog().getClosing());
        }

        return message;
    }

    public static void updateSoundWithProviderUrl(IStateManager stateManager, AudioRequest request) {
        if (request == null) {
            return;
        }

        ContentProviderService contentProviderService = stateManager.getApplicationState().getScopeValue("contentProviderService");

        String audioKey = getKey(request.getSound());
        String soundUrl = contentProviderService.resolveContent(stateManager.getDeviceId(), audioKey);
        request.setSound(soundUrl);
    }

    public static String getKey(String sound) {
        return AUDIO_CONTENT_ROOT + sound;
    }
}
