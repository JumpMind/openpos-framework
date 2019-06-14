package org.jumpmind.pos.core.model;

import java.util.Date;

public class DateField extends FormField {

    private static final long serialVersionUID = -1595522066135499584L;

    private boolean hideCalendar;
    private Date maxDate;
    private Date minDate;

    public DateField() {
        setInputType(FieldInputType.Date);
        setElementType(FieldElementType.Input);
        setPattern(FieldPattern.DATE);
    }
    
    public DateField(String id, String label) {
        this();
        this.setId(id);
        this.setLabel(label);
    }
    
    public DateField(String id, String label, boolean required) {
        this(id, label);
        this.setRequired(required);
    }
    
    public DateField(String id, String label, boolean required, boolean hideCalendar) {
        this(id, label, required);
        this.setHideCalendar(hideCalendar);
    }
    
    public DateField(String id, String label, boolean required, boolean hideCalendar, Date minDate) {
        this(id, label, required, hideCalendar);
        this.setMinDate(minDate);
    }
    
    public DateField(String id, String label, boolean required, boolean hideCalendar, Date minDate, Date maxDate) {
        this(id, label, required, hideCalendar, minDate);
        this.setMaxDate(maxDate);
    }
    
    public boolean isHideCalendar() {
        return hideCalendar;
    }

    public void setHideCalendar(boolean hideCalendar) {
        this.hideCalendar = hideCalendar;
    }
    
    public Date getMaxDate() {
        return maxDate;
    }

    public void setMaxDate(Date maxDate) {
        this.maxDate = maxDate;
    }

    public Date getMinDate() {
        return minDate;
    }

    public void setMinDate(Date minDate) {
        this.minDate = minDate;
    }

}