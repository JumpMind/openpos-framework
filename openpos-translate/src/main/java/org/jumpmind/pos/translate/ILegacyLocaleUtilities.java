package org.jumpmind.pos.translate;

import java.util.Locale;


public interface ILegacyLocaleUtilities {
    String formatComplexMessage(String pattern, Object[] vars);
    Locale getLocale(String localeString);
    Locale getCurrentLocale();
    String formatDecimalForWholeNumber(Number decimalNumber, Locale locale);
}
