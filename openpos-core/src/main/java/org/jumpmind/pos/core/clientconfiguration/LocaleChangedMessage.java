package org.jumpmind.pos.core.clientconfiguration;

import java.util.Locale;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

public class LocaleChangedMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String locale;

    String[] supportedLocales;

    public LocaleChangedMessage() {
        super(MessageType.LocaleChanged);
    }

    public LocaleChangedMessage(String locale) {
        this();
        this.locale = locale;
    }

    public LocaleChangedMessage(Locale locale) {
        this();
        setLocale(locale);
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale.toString();
    }

    public String[] getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(String[] supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

}
