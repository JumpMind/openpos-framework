package org.jumpmind.pos.core.template;

public class SelfCheckoutTemplate extends AbstractTemplate {

    private static final long serialVersionUID = 1L;

    boolean autoFocusOnScan = true;
    String scanSomethingText = "Scan/Key Something";
    StatusBar statusBar = new StatusBar();
    Scan scan = new Scan();

    public SelfCheckoutTemplate() {
        super("SelfCheckout");
    }

    public void setAutoFocusOnScan(boolean autoFocusOnScan) {
        this.autoFocusOnScan = autoFocusOnScan;
    }

    public boolean isAutoFocusOnScan() {
        return autoFocusOnScan;
    }

    public void setScanSomethingText(String scanSomethingText) {
        this.scanSomethingText = scanSomethingText;
    }

    public String getScanSomethingText() {
        return scanSomethingText;
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

}
