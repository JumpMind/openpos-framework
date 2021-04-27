package org.jumpmind.pos.core.ui.data;

import lombok.Data;
import org.jumpmind.pos.core.ui.message.SelectableItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SelectionListItem extends SelectableItem implements Serializable {
    private static final long serialVersionUID = 363696137283106343L;

    private String title;
    private List<SelectionListItemDisplayProperty> properties;
    private String itemImageUrl;

    public SelectionListItem() {
        this.properties = new ArrayList<>();
    }

    public SelectionListItem(String title) {
        this.title = title;
        this.properties = new ArrayList<>();
    }

    public SelectionListItem(String title, SelectionListItemDisplayProperty property) {
        this.title = title;
        this.properties = new ArrayList<>();
        this.properties.add(property);
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
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}