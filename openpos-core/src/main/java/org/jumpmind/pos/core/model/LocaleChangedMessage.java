package org.jumpmind.pos.core.model;

import java.util.Locale;

import org.jumpmind.pos.util.model.Message;

public class LocaleChangedMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String locale;

    public LocaleChangedMessage() {
        super(MessageType.LocaleChanged);
    }

    public LocaleChangedMessage(String locale) {
        this();
        this.locale = locale;
    }

    public LocaleChangedMessage(Locale locale) {
        this();
        this.locale = locale.getLanguage();
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale.getLanguage();
    }

}
