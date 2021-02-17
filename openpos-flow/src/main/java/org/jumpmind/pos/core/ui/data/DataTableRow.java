package org.jumpmind.pos.core.ui.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataTableRow implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<List<Object>> columns = new ArrayList<>();

    public DataTableRow() {
    }

    public DataTableRow(Object... columns) {
        for (Object column : columns) {
            List<Object> line = new ArrayList<>();
            line.add(column);
            this.columns.add(line);
        }
    }

    public List<List<Object>> getColumns() {
        return columns;
    }

    public void setColumns(List<List<Object>> columns) {
        this.columns = columns;
    }

    public void addColumn(String... lines) {
        List<Object> column = new ArrayList<>();
        for (String line : lines) {
            column.add(line);
        }
        this.columns.add(column);
    }

    public void addColumn(Object... lines) {
        List<Object> column = new ArrayList<>();
        for (Object line : lines) {
            column.add(line);
        }
        this.columns.add(column);
    }

    public void addColumn(List<Object> lines) {
        this.columns.add(lines);
    }
}