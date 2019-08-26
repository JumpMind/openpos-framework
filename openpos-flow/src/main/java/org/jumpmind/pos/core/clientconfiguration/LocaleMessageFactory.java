package org.jumpmind.pos.core.clientconfiguration;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocaleMessageFactory {

    @Value("${openpos.ui.language.supportedLocales:null}")
    String[] supportedLocales;

    @Value("${openpos.ui.language.showIcons:true}")
    boolean showIcons;

    @Autowired(required = false)
    ILocaleProvider localeProvider;

    public LocaleChangedMessage getMessage(Locale locale, Locale displayLocale) {
        LocaleChangedMessage message = new LocaleChangedMessage(locale, displayLocale);
        message.setSupportedLocales(supportedLocales);
        message.setShowIcons(showIcons);
        return message;
    }

    public LocaleChangedMessage getMessage() {
        LocaleChangedMessage message = null;
        if (localeProvider != null) {
            message = new LocaleChangedMessage(localeProvider.getLocale(), localeProvider.getLocale());
        } else {
            message = new LocaleChangedMessage();
        }
        message.setSupportedLocales(supportedLocales);
        message.setShowIcons(showIcons);
        return message;
    }
}
