package org.jumpmind.pos.core.screen;

import java.util.ArrayList;
import java.util.List;
import org.jumpmind.pos.core.screenpart.BaconStripPart;
import org.jumpmind.pos.core.screenpart.StatusStripPart;

public class DataTableScreen extends Screen {

    private static final long serialVersionUID = 1L;

    private BaconStripPart baconStrip = new BaconStripPart();
    private List<ActionItem> sausageLinks = new ArrayList<>();
    private StatusStripPart statusStrip = new StatusStripPart();

    private List<String> columnHeaders = new ArrayList<String>();
    private List<DataTableRow> rows = new ArrayList<>();

    private ActionItem actionButton;

    public DataTableScreen() {
        this.setScreenType(ScreenType.DataTable);
        this.setTemplate(null);
    }

    public BaconStripPart getBaconStrip() {
        return baconStrip;
    }

    public void setBaconStrip(BaconStripPart baconStrip) {
        this.baconStrip = baconStrip;
    }

    public List<ActionItem> getSausageLinks() {
        return sausageLinks;
    }

    public void setSausageLinks(List<ActionItem> sausageLinks) {
        this.sausageLinks = sausageLinks;
    }

    public StatusStripPart getStatusStrip() {
        return statusStrip;
    }

    public void setStatusStrip(StatusStripPart statusStrip) {
        this.statusStrip = statusStrip;
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

    public ActionItem getActionButton() {
        return actionButton;
    }

    public void setActionButton(ActionItem actionButton) {
        this.actionButton = actionButton;
    }

}
