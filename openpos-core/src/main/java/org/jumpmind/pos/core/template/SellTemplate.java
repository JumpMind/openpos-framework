package org.jumpmind.pos.core.template;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.screen.MenuItem;
import org.jumpmind.pos.core.screen.Workstation;
import org.jumpmind.pos.core.template.Scan.ScanType;

public class SellTemplate extends AbstractTemplate {

    protected static final long serialVersionUID = 1L;

    protected StatusBar statusBar = new StatusBar();
    
    protected List<MenuItem> localMenuItems = new ArrayList<>();
    
    protected List<MenuItem> transactionMenuItems = new ArrayList<>();
    
    Scan scan;
    
    private Workstation workstation;
    private String operatorText;

    public SellTemplate() {
        super("Sell");
    }

    public void setStatusBar(StatusBar statusBar) {
        this.statusBar = statusBar;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void setScan(Scan scan) {
        this.scan = scan;
    }

    public Scan getScan() {
        return scan;
    }

    public SellTemplate enableScan(boolean autoFocusOnScan) {
        Scan scan = new Scan();
        scan.setAutoFocusOnScan(autoFocusOnScan);
        scan.setScanType(ScanType.CAMERA_CORDOVA);
        scan.setScanActionName("Scan");
        setScan(scan);
        return this;
    }
    
    public MenuItem getLocalMenuItemByAction(String action) {
        return this. localMenuItems.stream().filter( mi -> action.equalsIgnoreCase(mi.getAction())).findFirst().orElse(null);
    }
    
    public MenuItem getLocalMenuItemByTitle(String title) {
        return this.localMenuItems.stream().filter( mi -> title.equalsIgnoreCase(mi.getTitle())).findFirst().orElse(null);
    }

    public void addLocalMenuItem(MenuItem menuItem) {
        this.localMenuItems.add(menuItem);
    }
    
    public void setLocalMenuItems(List<MenuItem> localMenuItems) {
        this.localMenuItems = localMenuItems;
    }
    
    public List<MenuItem> getLocalMenuItems() {
        return localMenuItems;
    }
    
    public void addTransactionMenuItem(MenuItem menuItem) {
        this.transactionMenuItems.add(menuItem);
    }
    
    public void setTransactionMenuItems(List<MenuItem> transactionMenuItems) {
        this.transactionMenuItems = transactionMenuItems;
    }
    
    public List<MenuItem> getTransactionMenuItems() {
        return transactionMenuItems;
    }
    

    public String getOperatorText() {
        return operatorText;
    }

    public void setOperatorText(String operatorText) {
        this.operatorText = operatorText;
    }

    public Workstation getWorkstation() {
        return workstation;
    }

    public void setWorkstation(Workstation workstation) {
        this.workstation = workstation;
    }       

}
