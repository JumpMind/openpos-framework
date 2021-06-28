package org.jumpmind.pos.core.flow;

import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.jumpmind.pos.core.audio.IAudioService;
import org.jumpmind.pos.core.service.ClientLocaleService;
import org.jumpmind.pos.server.model.Action;
import org.springframework.beans.factory.annotation.Autowired;

public class LocaleSelectedGlobalActionHandler {

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @Autowired
    ClientLocaleService clientLocaleService;
    
    @Autowired
    IAudioService audioService;

    @OnGlobalAction
    public void onLocaleSelected(Action action) {
        Locale locale = LocaleUtils.toLocale(action.getData());
        audioService.setLocale(locale);
        clientLocaleService.setDisplayLocale(locale);
        stateManager.refreshScreen();
    }

}