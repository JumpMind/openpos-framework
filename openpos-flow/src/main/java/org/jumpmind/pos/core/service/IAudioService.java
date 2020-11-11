package org.jumpmind.pos.core.service;

public interface IAudioService {
    void play(String key);

    void play(String key, AudioOptions options);
}
