package org.jumpmind.pos.core.screen;

import java.io.Serializable;

import org.jumpmind.pos.core.model.Form;

public class OptionItem implements IUIAction, Serializable {

    private static final long serialVersionUID = 1L;

    private String displayValue;
    private String value;
    private boolean enabled = true;
    private boolean selected = false;
    private String icon;

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
        this.value = value;
        this.displayValue = displayValue;
        this.enabled = enabled;
        this.icon = icon;
    }

    public OptionItem(String value, String displayValue, boolean enabled, IIcon icon) {
        this(value, displayValue, enabled, icon.getName());
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String getAction() {
        return this.getValue();
    }

    @Override
    public void setAction(String action) {
        this.setValue(action);
    }

    @Override
    public String getTitle() {
        return this.getDisplayValue();
    }

    @Override
    public void setTitle(String title) {
        this.setDisplayValue(title);
    }
    
    public void setForm(Form form) {
        this.form = form;
    }

    public Form getForm() {
        return form;
    }

}
