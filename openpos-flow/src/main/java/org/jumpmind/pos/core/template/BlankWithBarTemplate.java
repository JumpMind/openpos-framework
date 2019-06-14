package org.jumpmind.pos.core.template;

import org.jumpmind.pos.core.screen.Workstation;
import org.jumpmind.pos.core.template.Scan.ScanType;

public class BlankWithBarTemplate extends AbstractTemplate {

    private static final long serialVersionUID = 1L;

    Scan scan;
    private Workstation workstation;
    private String operatorText;
    protected StatusBar statusBar = new StatusBar();

    public BlankWithBarTemplate() {
        super("BlankWithBar");
    }

    public BlankWithBarTemplate enableScan(boolean autoFocusOnScan) {
        Scan scan = new Scan();
        scan.setAutoFocusOnScan(autoFocusOnScan);
        scan.setScanType(ScanType.CAMERA_CORDOVA);
        scan.setScanActionName("Scan");
        setScan(scan);
        return this;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public Scan getScan() {
        return scan;
    }

    public Workstation getWorkstation() {
        return workstation;
    }

    public void setWorkstation(Workstation workstation) {
        this.workstation = workstation;
    }

    public String getOperatorText() {
        return operatorText;
    }

    public void setOperatorText(String operatorText) {
        this.operatorText = operatorText;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

}
