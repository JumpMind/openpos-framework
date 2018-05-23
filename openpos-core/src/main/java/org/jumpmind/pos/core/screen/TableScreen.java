package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;


public class TableScreen extends Screen {

    private static final long serialVersionUID = 1L;    
    
    private List<String> headerLabels;
    private List<List<String>> tableData = new ArrayList<>();
    private String text;
    private SelectionMode selectionMode = SelectionMode.None;
    private int selectedRow = -1;
    private String selectAction;
    
    public TableScreen() {
        setType(ScreenType.Table);
    }

    public void setSelectAction(String selectAction) {
        this.selectAction = selectAction;
    }
    
    public String getSelectAction() {
        return selectAction;
    }
    
    public List<String> getHeaderLabels() {
        return headerLabels;
    }
    
    public void setHeaderLabels(String...headers) {
        headerLabels = new ArrayList<>(headers.length);
        for (String string : headers) {
            headerLabels.add(string);
        }
    }
    
    public void setHeaderLabels(List<String> headerLabels) {
        this.headerLabels = headerLabels;
    }
    
    public List<List<String>> getTableData() {
        return tableData;
    }
    
    public void setTableData(List<List<String>> tableData) {
        this.tableData = tableData;
    }
    
    public void addRow(String...row) {
        List<String> tableRow = new ArrayList<>();
        for (String string : row) {
            tableRow.add(string);
        }
        addRow(tableRow);
    }
    
    public void addRow(List<String> tableRow) {
        this.getTableData().add(tableRow);
    }
    
    public String getText() {
        return this.text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
 
    public SelectionMode getSelectionMode() {
        return selectionMode;
    }
    
    public void setSelectionMode(SelectionMode selectionMode) {
        this.selectionMode = selectionMode;
    }

    public void setSelectedRow(int selectedRow) {
       this.selectedRow = selectedRow;
    }
    
    public int getSelectedRow() {
        return this.selectedRow;
    }
    
}
