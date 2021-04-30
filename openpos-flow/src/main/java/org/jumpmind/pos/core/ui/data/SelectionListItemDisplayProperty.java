package org.jumpmind.pos.core.ui.data;

import lombok.Data;
import org.jumpmind.pos.core.ui.message.SelectableItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class SelectionListItemDisplayProperty extends SelectableItem implements Serializable {
    private static final long serialVersionUID = -3667174964838343444L;

    private String label;
    private List<String> lines;
    private String textAlignment = TextAlignment.Left;

    public SelectionListItemDisplayProperty() {
        this.lines = new ArrayList<>();
    }

    public SelectionListItemDisplayProperty(String label) {
        this.label = label;
    }

    public SelectionListItemDisplayProperty(String label, String line) {
        this.label = label;
        this.lines = new ArrayList<>();
        this.lines.add(line);
    }

    public SelectionListItemDisplayProperty(List<String> columnRows) {
        this.lines = columnRows;
    }

    public void addLine(String line) {
        if (this.lines == null) {
            this.lines = new ArrayList<>();
        }
        this.lines.add(line);
    }
}