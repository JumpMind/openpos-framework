package org.jumpmind.pos.core.screenpart;

import java.io.Serializable;

import org.jumpmind.pos.core.model.FieldInputType;

public class ScanPart implements Serializable{
    private static final long serialVersionUID = 1L;

    public enum ScanType {
        CAMERA_CORDOVA, NONE
    }

    private Integer scanMinLength = 1;
    private Integer scanMaxLength = 22;
    private ScanType scanType;
    private String scanIcon;
    private String scanActionName = "Scan";
    private String scanSomethingText = "Scan/Key Something";
    private boolean autoFocusOnScan = false;
    private FieldInputType inputType = FieldInputType.AlphanumericText;
    
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

    public FieldInputType getInputType() {
        return inputType;
    }

    public void setInputType(FieldInputType inputType) {
        this.inputType = inputType;
    }

    public String getScanIcon() {
        return scanIcon;
    }

    public void setScanIcon(String scanIcon) {
        this.scanIcon = scanIcon;
    }
}
