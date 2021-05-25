package org.jumpmind.pos.core.service;

import java.util.Locale;

import org.jumpmind.pos.core.clientconfiguration.LocaleChangedMessage;
import org.jumpmind.pos.core.clientconfiguration.LocaleMessageFactory;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.server.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("device")
public class ClientLocaleService {

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @Autowired
    MessageService messageService;

    @Autowired
    LocaleMessageFactory localeMessageFactory;

    private Locale locale;

    private Locale displayLocale;

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        sendMessage();
    }

    public Locale getDisplayLocale() {
        return displayLocale;
    }

    public void setDisplayLocale(Locale displayLocale) {
        this.displayLocale = displayLocale;
        sendMessage();
    }

    private void sendMessage() {
        LocaleChangedMessage message = localeMessageFactory.getMessage(locale, displayLocale);
        messageService.sendMessage(stateManager.getDeviceId(), message);
    }

}
