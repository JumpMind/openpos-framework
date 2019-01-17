package org.jumpmind.pos.core.screenpart;

import java.io.Serializable;

import org.jumpmind.pos.core.screen.MenuItem;

public class BaconStripPart implements Serializable{

    private static final long serialVersionUID = 1L;

    private String deviceId;
    private String operatorText;
    private String headerText;
    private String headerIcon;
    private MenuItem backButton;
    
    public String getDeviceId() {
        return deviceId;
    }
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getOperatorText() {
        return operatorText;
    }
    public void setOperatorText(String operatorText) {
        this.operatorText = operatorText;
    }
    public String getHeaderText() {
        return headerText;
    }
    public void setHeaderText(String headerText) {
        this.headerText = headerText;
    }
    public String getHeaderIcon() {
        return headerIcon;
    }
    public void setHeaderIcon(String headerIcon) {
        this.headerIcon = headerIcon;
    }
    public MenuItem getBackButton() {
        return backButton;
    }
    public void setBackButton(MenuItem backButton) {
        this.backButton = backButton;
    }
}
