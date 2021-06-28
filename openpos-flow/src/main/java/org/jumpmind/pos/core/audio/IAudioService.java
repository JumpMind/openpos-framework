package org.jumpmind.pos.core.audio;

import java.util.Locale;

public interface IAudioService {
    void play(String sound);

    void play(AudioRequest request);
    
    void setLocale(Locale locale);
}
