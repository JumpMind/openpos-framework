package org.jumpmind.pos.core.model;

public final class FieldPattern {
    public static final String EMAIL =  "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";
    public static final String MONEY =  "^(\\d{0,9}\\.\\d{0,2}|\\d{1,9})$";
    public static final String PERCENT =  "^100$|^\\d{0,2}(\\.\\d{1,2})?$|^\\d{0,2}(\\.)?"; // 100-0, Only two decimal places allowed.
    public static final String DATE = "^(\\d{2})/(\\d{2})/(\\d{4}$)";
    public static final String YY_DATE = "^(\\d{2})/(\\d{2})/(\\d{2}$)";
    public static final String NO_YEAR_DATE = "^(\\d{2})/(\\d{2})$";
    public static final String US_PHONE_NUMBER = "^\\d{10}$";
    public static final String WORD_CHARS = "^[a-zA-Z0-9]+$";

}
