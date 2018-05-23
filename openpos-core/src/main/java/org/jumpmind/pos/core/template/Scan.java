package org.jumpmind.pos.core.template;

import java.io.Serializable;

public class Scan implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum ScanType {
        CAMERA_CORDOVA, NONE
    }

    protected Integer scanMinLength = 1;
    protected Integer scanMaxLength = 20;
    protected ScanType scanType;
    protected String scanActionName;
    protected String scanSomethingText = "Scan/Key Something";
    protected boolean autoFocusOnScan = false;

    public Integer getScanMinLength() {
        return scanMinLength;
    }

    public void setScanMinLength(Integer scanMinLength) {
        this.scanMinLength = scanMinLength;
    }

    public Integer getScanMaxLength() {
        return scanMaxLength;
    }

    public void setScanMaxLength(Integer scanMaxLength) {
        this.scanMaxLength = scanMaxLength;
    }

    public ScanType getScanType() {
        return scanType;
    }

    public void setScanType(ScanType scanType) {
        this.scanType = scanType;
    }

    public String getScanActionName() {
        return scanActionName;
    }

    public void setScanActionName(String scanActionName) {
        this.scanActionName = scanActionName;
    }

    public String getScanSomethingText() {
        return scanSomethingText;
    }

    public void setScanSomethingText(String scanSomethingText) {
        this.scanSomethingText = scanSomethingText;
    }

    public boolean isAutoFocusOnScan() {
        return autoFocusOnScan;
    }

    public void setAutoFocusOnScan(boolean autoFocusOnScan) {
        this.autoFocusOnScan = autoFocusOnScan;
    }

}
