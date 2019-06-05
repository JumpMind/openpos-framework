package org.jumpmind.pos.core.flow;

import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.jumpmind.pos.core.model.LocaleChangedMessage;
import org.jumpmind.pos.core.service.ClientLocaleService;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

public class LanguageSelectedGlobalActionHandler {

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @Autowired
    ClientLocaleService clientLocaleService;

    @Autowired
    MessageService messageService;

    @OnGlobalAction
    public void handleAction(Action action) {

        Locale locale = LocaleUtils.toLocale(action.getData());
        clientLocaleService.setLocale(locale);
        messageService.sendMessage(stateManager.getAppId(), stateManager.getDeviceId(), new LocaleChangedMessage(locale));
        stateManager.refreshScreen();
    }

}
