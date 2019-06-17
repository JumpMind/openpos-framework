package org.jumpmind.pos.core.screen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataTableRow implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<List<String>> columns = new ArrayList<>();

    public DataTableRow() {
    }

    public DataTableRow(String... columns) {
        for (String column : columns) {
            List<String> line = new ArrayList<>();
            line.add(column);
            this.columns.add(line);
        }
    }

    public List<List<String>> getColumns() {
        return columns;
    }

    public void setColumns(List<List<String>> columns) {
        this.columns = columns;
    }

    public void addColumn(String... lines) {
        List<String> column = new ArrayList<>();
        for (String line : lines) {
            column.add(line);
        }
        this.columns.add(column);
    }

    public void addColumn(List<String> lines) {
        this.columns.add(lines);
    }

}
