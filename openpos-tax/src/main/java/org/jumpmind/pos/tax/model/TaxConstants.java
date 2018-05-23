package org.jumpmind.pos.tax.model;

public class TaxConstants {

    /* TaxAuthority.roundingCode */

    public static final String ROUNDING_UP = "1";

    public static final String ROUNDING_DOWN = "2";

    public static final String ROUNDING_HALF_UP = "3";

    public static final String ROUNDING_HALF_DOWN = "4";

    /* TaxGroupRule.calculationMethodCode */

    public static final String CALCULATION_TRANSACTION = "1";

    public static final String CALCULATION_ITEM_TRANSACTION = "2";

    public static final String CALCULATION_ITEM = "3";

    /* TaxGroupRule.taxRateRuleUsageCode */

    public static final String USAGE_PICK_ONE = "1";

    public static final String USAGE_TAX_TABLE = "2";

    /* TaxRateRule.typeCode */

    public static final String TYPE_PERCENT = "1";

    public static final String TYPE_FLAT = "2";

}
