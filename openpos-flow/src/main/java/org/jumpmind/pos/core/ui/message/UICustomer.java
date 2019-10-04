package org.jumpmind.pos.core.ui.message;

import java.io.Serializable;

public class UICustomer implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String id;
    private String label;
    private String icon;

    public UICustomer(String name) {
        this.name = name;
    }

    public UICustomer(String name, String id, String label, String icon) {
        this.name = name;
        this.id = id;
        this.label = label;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

}