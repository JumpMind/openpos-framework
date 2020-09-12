package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.data.DataTableRow;

import java.util.ArrayList;
import java.util.List;

public class DataTableUIMessage extends UIMessage {

    private String instructions;

    private List<String> columnHeaders = new ArrayList<String>();
    private List<DataTableRow> rows = new ArrayList<>();

    private List<ActionItem> actionButtons;

    public DataTableUIMessage() {
        this.setScreenType(UIMessageType.DATA_TABLE);
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public List<String> getColumnHeaders() {
        return columnHeaders;
    }

    public void setColumnHeaders(List<String> columnHeaders) {
        this.columnHeaders = columnHeaders;
    }

    public void setColumnHeaders(String... columnHeaders) {
        this.columnHeaders.clear();
        for (String column : columnHeaders) {
            this.columnHeaders.add(column);
        }
    }

    public void addColumnHeader(String columnHeader) {
        this.columnHeaders.add(columnHeader);
    }

    public List<DataTableRow> getRows() {
        return rows;
    }

    public void setRows(List<DataTableRow> rows) {
        this.rows = rows;
    }

    public void addRow(String... columns) {
        this.rows.add(new DataTableRow(columns));
    }

    public void addRow(DataTableRow row) {
        this.rows.add(row);
    }

    public List<ActionItem> getActionButtons() {
        return actionButtons;
    }

    public void setActionButtons(List<ActionItem> actionButtons) {
        this.actionButtons = actionButtons;
    }

    public void addActionButton(ActionItem actionButton) {
        if(this.actionButtons == null) {
            this.actionButtons = new ArrayList<>();
        }
        this.actionButtons.add(actionButton);
    }

}
