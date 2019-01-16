package org.jumpmind.pos.core.model;

public enum FieldInputType {
    AlphanumericPassword,
    /** Allows alphabetic characters, numeric characters, and special characters */
    AlphanumericText,
    GiftCode,
    NumericText,
    Checkbox,
    ComboBox,
    PopTart,
    SearchablePopTart,
    Decimal,
    TextArea,
    StateIDNumber,
    /** Allows positive decimal and integer values with at most two decimal places */
    Percent,
    /** Only allows values of 100, 0 or 2 digit positive integer percentage */
    PercentInt,
    USPostalCode,
    PostalCode,
    Phone,
    Money,
    Income,
    /** MM/dd/yyyy format */
    Date,
    /** MM/dd format */
    NoYearDate,
    DateMMDDYY,
    DateDDMMYY,
    DateDDMMYYYY,
    ToggleButton,
    AutoComplete,
    /** Allows a-z, A-Z, 0-9 */
    WordText,
    Counter,
    DatePartChooser,
    Time
    ;
    
    public static FieldInputType toDateInputType(String dateFormat) {
        switch (dateFormat) {
            case "MMdd":
            case "MM/dd":
                return NoYearDate;
            case "MMddyy":
            case "MM/dd/yy":
                return DateMMDDYY;
            case "ddMMyyyy":
            case "dd/MM/yyyy":
                return DateDDMMYYYY;
            case "ddMMyy":
            case "dd/MM/yy":
                return DateDDMMYY;
            case "MMddyyyy":
            case "MM/dd/yyyy":
            default:
                return Date;
        }
    }
}
