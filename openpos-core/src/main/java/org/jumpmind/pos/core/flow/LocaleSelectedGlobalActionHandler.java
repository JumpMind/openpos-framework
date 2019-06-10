package org.jumpmind.pos.core.flow;

import java.util.Locale;

import org.apache.commons.lang3.LocaleUtils;
import org.jumpmind.pos.core.clientconfiguration.LocaleChangedMessage;
import org.jumpmind.pos.core.clientconfiguration.LocaleMessageFactory;
import org.jumpmind.pos.core.service.ClientLocaleService;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;

public class LocaleSelectedGlobalActionHandler {

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @Autowired
    ClientLocaleService clientLocaleService;

    @Autowired
    MessageService messageService;

    @Autowired
    LocaleMessageFactory localeMessageFactory;

    @OnGlobalAction
    public void handleAction(Action action) {
        Locale locale = LocaleUtils.toLocale(action.getData());
        clientLocaleService.setLocale(locale);
        LocaleChangedMessage message = localeMessageFactory.getMessage(locale);
        messageService.sendMessage(stateManager.getAppId(), stateManager.getDeviceId(), message);
        stateManager.refreshScreen();
    }

}
