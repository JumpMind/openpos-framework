package org.jumpmind.pos.core.template;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.ActionItemGroup;
import org.jumpmind.pos.core.screen.Workstation;
import org.jumpmind.pos.core.template.Scan.ScanType;

public class SellTemplate extends AbstractTemplate {

    protected static final long serialVersionUID = 1L;

    protected StatusBar statusBar = new StatusBar();
    
    protected List<ActionItem> localMenuItems = new ArrayList<>();
    
    private String transactionMenuPrompt;
    private ActionItemGroup transactionMenu = new ActionItemGroup();
    
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
    
    public ActionItem getLocalMenuItemByAction(String action) {
        return this. localMenuItems.stream().filter( mi -> action.equalsIgnoreCase(mi.getAction())).findFirst().orElse(null);
    }
    
    public ActionItem getLocalMenuItemByTitle(String title) {
        return this.localMenuItems.stream().filter( mi -> title.equalsIgnoreCase(mi.getTitle())).findFirst().orElse(null);
    }

    public void addLocalMenuItem(ActionItem menuItem) {
        this.localMenuItems.add(menuItem);
    }
    
    public void setLocalMenuItems(List<ActionItem> localMenuItems) {
        this.localMenuItems = localMenuItems;
    }
    
    public List<ActionItem> getLocalMenuItems() {
        return localMenuItems;
    }
    
    public String getTransactionMenuPrompt() {
        return transactionMenuPrompt;
    }

    public void setTransactionMenuPrompt(String transactionMenuPrompt) {
        this.transactionMenuPrompt = transactionMenuPrompt;
    }

    public void addTransactionMenuItem(ActionItem menuItem) {
        this.transactionMenu.getActionItems().add(menuItem);
    }
    
    public void setTransactionMenu(ActionItemGroup transactionMenu) {
        this.transactionMenu = transactionMenu;
    }
    
    public ActionItemGroup getTransactionMenu() {
        return transactionMenu;
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
