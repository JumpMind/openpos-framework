package org.jumpmind.pos.core.clientconfiguration;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocaleMessageFactory {

    @Value("${openpos.ui.language.supportedLocales:null}")
    String[] supportedLocales;

    @Autowired(required = false)
    ILocaleProvider localeProvider;

    public LocaleChangedMessage getMessage(Locale locale, Locale displayLocale) {
        if (localeProvider != null) {
            if (locale == null) {
                locale = localeProvider.getLocale();
            }
            if (displayLocale == null) {
                displayLocale = localeProvider.getLocale();
            }
        }
        LocaleChangedMessage message = new LocaleChangedMessage(locale, displayLocale);
        message.setSupportedLocales(supportedLocales);
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
        return message;
    }
}
