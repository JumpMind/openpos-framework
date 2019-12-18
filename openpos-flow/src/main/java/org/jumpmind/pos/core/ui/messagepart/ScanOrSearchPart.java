package org.jumpmind.pos.core.ui.messagepart;

import org.jumpmind.pos.core.model.FieldInputType;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IconType;

import java.io.Serializable;

public class ScanOrSearchPart implements Serializable{
    private static final long serialVersionUID = 1L;

    public enum ScanType {
        CAMERA_CORDOVA, NONE
    }

    private Integer scanMinLength = 1;
    private Integer scanMaxLength = 22;
    private ScanType scanType;
    private String scanIcon = IconType.Barcode;
    private ActionItem scanAction = new ActionItem("Scan");
    private ActionItem keyedAction = new ActionItem("Scan");
    private String scanSomethingText = "Scan/Key Something";
    private boolean autoFocusOnScan = false;
    private FieldInputType inputType = FieldInputType.WordText;
    private String keyboardLayout;
    
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

    public ActionItem getScanAction() {
        return scanAction;
    }

    public void setScanAction(ActionItem scanAction) {
        this.scanAction = scanAction;
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

    public void setKeyedAction(ActionItem keyedAction) {
        this.keyedAction = keyedAction;
    }

    public ActionItem getKeyedAction() {
        return this.keyedAction;
    }

    public String getKeyboardLayout() {
        return keyboardLayout;
    }

    public void setKeyboardLayout(String keyboardLayout) {
        this.keyboardLayout = keyboardLayout;
    }
}
