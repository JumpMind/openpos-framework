package org.jumpmind.pos.core.ui.data;

import org.jumpmind.pos.core.ui.data.SelectionListItemDisplayProperty;
import org.jumpmind.pos.core.ui.message.SelectableItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectionListItem extends SelectableItem implements Serializable{

    private static final long serialVersionUID = 363696137283106343L;

    private String title;

    private List<SelectionListItemDisplayProperty> properties;

    private String itemImageUrl;

    public SelectionListItem() {
        this.properties = new ArrayList<SelectionListItemDisplayProperty>();
    }

    public SelectionListItem(String title) {
        this.title = title;
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
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isEnabled() {
        return  enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

}