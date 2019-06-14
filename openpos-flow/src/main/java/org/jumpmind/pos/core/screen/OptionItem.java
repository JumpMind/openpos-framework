package org.jumpmind.pos.core.screen;

import java.io.Serializable;

import org.jumpmind.pos.core.model.Form;

public class OptionItem extends ActionItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean selected = false;

    private Form form = new Form();

    public OptionItem() {
    }

    public OptionItem(String value) {
        this(value, value);
    }

    public OptionItem(String value, String displayValue) {
        this(value, displayValue, true);
    }

    public OptionItem(String value, String displayValue, boolean enabled) {
        this(value, displayValue, enabled, (String) null);
    }
    
    public OptionItem(String value, String displayValue, String icon) {
        this(value, displayValue, true, icon);
    }

    public OptionItem(String value, String displayValue, boolean enabled, String icon) {
        this.setValue(value);
        this.setDisplayValue(displayValue);
        this.setEnabled(enabled);
        this.setIcon(icon);
    }

    public String getDisplayValue() {
        return this.getTitle();
    }

    public void setDisplayValue(String displayValue) {
       this.setTitle(displayValue);
    }

    public String getValue() {
        return this.getAction();
    }

    public void setValue(String value) {
        this.setAction(value);
    }

    public void disable() {
        this.setEnabled(false);
    }
    
    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setForm(Form form) {
        this.form = form;
    }

    public Form getForm() {
        return form;
    }

    public OptionItem keybind(String keybind) {
        this.setKeybind(keybind);
        return this;
    }

}
