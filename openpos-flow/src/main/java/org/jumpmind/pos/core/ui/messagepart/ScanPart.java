package org.jumpmind.pos.core.ui.messagepart;

import java.io.Serializable;

public class ScanPart implements Serializable {
    private static final long serialVersionUID = 1L;

    private String scanActionName = "Scan";

    public ScanPart(String scanActionName) {
        this.scanActionName = scanActionName;
    }

    public String getScanActionName() {
        return scanActionName;
    }

    public void setScanActionName(String scanActionName) {
        this.scanActionName = scanActionName;
    }

}
