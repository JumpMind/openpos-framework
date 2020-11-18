package org.jumpmind.pos.core.audio;

import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.core.content.ContentProviderService;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.util.ResourceUtils;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public final class AudioUtil {
    public static final String AUDIO_LICENSE = "licenses.csv";
    public static final String AUDIO_CONTENT_ROOT = "audio/";

    public static AudioConfigMessage getInteractionMessageFromConfig(IStateManager stateManager, AudioConfig config) {
        AudioConfig configCopy = (AudioConfig) config.clone();

        if (configCopy.getInteractions() != null && configCopy.getInteractions().getMouse() != null) {
            setProviderUrl(stateManager, configCopy.getInteractions().getMouse().getMouseDown());
            setProviderUrl(stateManager, configCopy.getInteractions().getMouse().getMouseUp());
        }

        if (configCopy.getInteractions() != null && configCopy.getInteractions().getDialog() != null) {
            setProviderUrl(stateManager, configCopy.getInteractions().getDialog().getOpening());
            setProviderUrl(stateManager, configCopy.getInteractions().getDialog().getClosing());
        }

        return new AudioConfigMessage(configCopy);
    }

    public static void setProviderUrl(IStateManager stateManager, AudioRequest request) {
        if (request == null) {
            return;
        }

        ContentProviderService contentProviderService = stateManager.getApplicationState().getScopeValue("contentProviderService");

        String audioKey = getKey(request.getSound());
        String soundUrl = contentProviderService.resolveContent(stateManager.getDeviceId(), audioKey);
        request.setUrl(soundUrl);
    }

    public static String getKey(String sound) {
        return AUDIO_CONTENT_ROOT + sound;
    }

    public static List<String> getAllContentKeys() {
        Resource[] resources;
        ArrayList<String> contentKeys = new ArrayList<>();

        try {
            resources = ResourceUtils.getContentResources(AudioUtil.AUDIO_CONTENT_ROOT + "**/*.*");
            Arrays.stream(resources)
                    .filter(resource -> !resource.getFilename().equals(AUDIO_LICENSE))
                    .forEach(resource -> {
                        try {
                            Path resourcePath = Paths.get(resource.getURI());
                            // The parent folder of each file is the content key
                            String parentFolder = resourcePath.getParent().getFileName().toString();
                            contentKeys.add(parentFolder);
                        } catch (IOException e) {
                            log.info("Unable to load audio content resources", e);
                        }
                    });
        } catch (IOException e) {
            log.info("Unable to load audio content resources", e);
        }

        return contentKeys;
    }

    public static List<String> getAllContentUrls(IStateManager stateManager) {
        ContentProviderService contentProviderService = stateManager.getApplicationState().getScopeValue("contentProviderService");

        return getAllContentKeys().stream()
                .map(contentKey -> contentProviderService.resolveContent(stateManager.getDeviceId(), AUDIO_CONTENT_ROOT + contentKey))
                .collect(Collectors.toList());
    }
}
