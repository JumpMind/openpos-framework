package org.jumpmind.pos.core.clientconfiguration;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocaleMessageFactory {

    @Value("${openpos.ui.language.supportedLocales:null}")
    String[] supportedLocales;

    public LocaleChangedMessage getMessage(Locale locale) {
        LocaleChangedMessage message = new LocaleChangedMessage(locale);
        message.setSupportedLocales(supportedLocales);
        return message;
    }

    public LocaleChangedMessage getMessage() {
        LocaleChangedMessage message = new LocaleChangedMessage();
        message.setSupportedLocales(supportedLocales);
        return message;
    }
}
