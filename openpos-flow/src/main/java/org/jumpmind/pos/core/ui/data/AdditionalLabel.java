package org.jumpmind.pos.core.ui.data;

import java.io.Serializable;

public class AdditionalLabel implements Serializable {

    private static final long serialVersionUID = 1L;

    private String label;
    private String value;

    public AdditionalLabel(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}