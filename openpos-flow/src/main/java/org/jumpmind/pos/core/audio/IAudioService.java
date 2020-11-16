package org.jumpmind.pos.core.audio;

public interface IAudioService {
    void play(String sound);

    void play(AudioRequest request);
}
