package org.jumpmind.pos.translate;

import java.math.BigDecimal;
import java.util.Locale;

public interface ILegacyCurrency {
    String[] getDenominationNames();
    String[] getDenominationDisplayNames(Locale paramLocale);
    String getDenominationValue(String paramString);
    String[] getDenominationValues();
    String toFormattedString();
    String toFormattedString(Locale paramLocale);
    String toGroupFormattedString();
    String toGroupFormattedString(Locale paramLocale);
    BigDecimal toBigDecimal();
    
    <T> T toTargetPlafCurrency();
}
