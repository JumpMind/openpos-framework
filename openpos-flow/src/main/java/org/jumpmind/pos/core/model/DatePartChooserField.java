package org.jumpmind.pos.core.model;

public class DatePartChooserField extends FormField {
    
    private static final long serialVersionUID = 1L;

    public enum DatePartChooserMode {
        MonthDate,
        /** FUTURE, Not yet supported */
        MonthYear,
        /** FUTURE, Not yet supported */
        MonthDateYear
    }
    
    private Integer month;
    private Integer dayOfMonth;
    private Integer year;
    private String formatter;
    private DatePartChooserMode mode = DatePartChooserMode.MonthDate;
    private String popupTitle;
    private boolean hideButtons;
    
    public DatePartChooserField() {
        setInputType(FieldInputType.DatePartChooser);
    }

    public DatePartChooserField(String fieldId) {
        this();
        this.setId(fieldId);
    }

    public DatePartChooserField(String fieldId, String label) {
        this(fieldId);
        this.setLabel(label);
    }

    public DatePartChooserField(String fieldId, String label, DateChooserValues value) {
        this(fieldId, label);
        if (value != null) {
            this.month = value.month;
            this.dayOfMonth = value.dayOfMonth;
            this.year = value.year;
        }
        
    }
    
    public DatePartChooserField(String fieldId, String label, DateChooserValues value, String placeholder) {
        this(fieldId, label, value);
        this.setPlaceholder(placeholder);
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
    
    public DatePartChooserField month(Integer month) {
        this.setMonth(month);
        return this;
    }

    public Integer getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(Integer dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public DatePartChooserField dayOfMonth(Integer dayOfMonth) {
        this.setDayOfMonth(dayOfMonth);
        return this;
    }
    
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
    
    @Override
    public void setValue(String value) {
        // Doesn't apply, use month, dayOfMonth, year fields
    }
    
    public String getFormatter() {
        return this.formatter;
    }
    
    public void setFormatter(String formatter) {
        this.formatter = formatter;
    }
    
    
    public DatePartChooserField year(Integer year) {
        this.setYear(year);
        return this;
    }

    public DatePartChooserMode getMode() {
        return mode;
    }

    public void setMode(DatePartChooserMode mode) {
        this.mode = mode;
    }

    public DatePartChooserField mode(DatePartChooserMode mode) {
        this.setMode(mode);
        return this;
    }
    
    public String getPopupTitle() {
        return popupTitle;
    }

    public void setPopupTitle(String popupTitle) {
        this.popupTitle = popupTitle;
    }

    public DatePartChooserField popupTitle(String popupTitle) {
        this.setPopupTitle(popupTitle);
        return this;
    }
    
    public boolean isHideButtons() {
        return hideButtons;
    }

    public void setHideButtons(boolean hideButtons) {
        this.hideButtons = hideButtons;
    }

    public static class DateChooserValues {
        public Integer month;
        public Integer dayOfMonth;
        public Integer year;

        public DateChooserValues() {
        }
        public DateChooserValues(Integer month) {
            this.month = month;
        }
        
        public DateChooserValues(Integer month, Integer dayOfMonth) {
            this(month);
            this.dayOfMonth = dayOfMonth;
        }
        
        public DateChooserValues(Integer month, Integer dayOfMonth, Integer year) {
            this(month, dayOfMonth);
            this.year = year;
        }
    }
}


