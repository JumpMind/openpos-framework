package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectionListItem implements Serializable {

    private static final long serialVersionUID = 363696137283106343L;

    private String title;

    private List<SelectionListItemDisplayProperty> properties;
    
    private boolean isSelected;

    public SelectionListItem() {
        this.properties = new ArrayList<SelectionListItemDisplayProperty>();
    }

    public SelectionListItem(String title, SelectionListItemDisplayProperty property) {
        this.title = title;
        this.properties = new ArrayList<SelectionListItemDisplayProperty>();
        this.properties.add(property);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SelectionListItemDisplayProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<SelectionListItemDisplayProperty> properties) {
        this.properties = properties;
    }
    
    public void addProperty(SelectionListItemDisplayProperty property) {
        this.properties.add(property);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

}