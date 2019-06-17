package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectionListItemDisplayProperty implements Serializable {

    private static final long serialVersionUID = -3667174964838343444L;

    private String label;

    private List<String> lines;

    public SelectionListItemDisplayProperty() {
        this.lines = new ArrayList<String>();
    }

    public SelectionListItemDisplayProperty(String label) {
        this.label = label;
    }

    public SelectionListItemDisplayProperty(String label, String line) {
        this.label = label;
        this.lines = new ArrayList<String>();
        this.lines.add(line);
    }

    public SelectionListItemDisplayProperty(List<String> columnRows) {
        this.lines = columnRows;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public void addLine(String line) {
        if (this.lines == null) {
            this.lines = new ArrayList<String>();
        }
        this.lines.add(line);
    }

}