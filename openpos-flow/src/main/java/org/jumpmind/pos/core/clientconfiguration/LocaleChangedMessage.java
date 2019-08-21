package org.jumpmind.pos.core.clientconfiguration;

import java.util.Locale;

import org.jumpmind.pos.core.model.MessageType;
import org.jumpmind.pos.util.model.Message;

public class LocaleChangedMessage extends Message {

    private static final long serialVersionUID = 1L;

    private String locale;

    private String displayLocale;

    String[] supportedLocales;
    
    private boolean showIcons = true;

    public LocaleChangedMessage() {
        super(MessageType.LocaleChanged);
    }

    public LocaleChangedMessage(Locale locale, Locale displayLocale) {
        this();
        setLocale(locale);
        setDisplayLocale(displayLocale);
}

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setLocale(Locale locale) {
        if (locale != null) {
            this.locale = locale.toString();
        }
    }

    public String getDisplayLocale() {
        return displayLocale;
    }

    public void setDisplayLocale(String displayLocale) {
        this.displayLocale = displayLocale;
    }

    public void setDisplayLocale(Locale displayLocale) {
        if (displayLocale != null) {
            this.displayLocale = displayLocale.toString();
        }
    }

    public String[] getSupportedLocales() {
        return supportedLocales;
    }

    public void setSupportedLocales(String[] supportedLocales) {
        this.supportedLocales = supportedLocales;
    }

    public boolean isShowIcons() {
        return showIcons;
    }

    public void setShowIcons(boolean showIcons) {
        this.showIcons = showIcons;
    }

}
