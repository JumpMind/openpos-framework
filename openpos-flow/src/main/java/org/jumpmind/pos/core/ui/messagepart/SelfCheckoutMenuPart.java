package org.jumpmind.pos.core.ui.messagepart;

import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.ui.IHasBackButton;

import java.io.Serializable;

public class SelfCheckoutMenuPart implements IHasBackButton, Serializable {

    private static final long serialVersionUID = 1L;

    private String deviceId;
    private String operatorText;
    private String headerText;
    private String headerIcon;
    private ActionItem backButton;
    private boolean showScan;
    private boolean showAdmin;
    private boolean showSkip;
    private boolean showLanguageSelector = true;

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

    public ActionItem getBackButton() {
        return backButton;
    }

    public void setBackButton(ActionItem backButton) {
        this.backButton = backButton;
    }

    public boolean isShowScan() {
        return showScan;
    }

    public void setShowScan(boolean showScan) {
        this.showScan = showScan;
    }

    public boolean isShowAdmin() {
        return showAdmin;
    }

    public void setShowAdmin(boolean showAdmin) {
        this.showAdmin = showAdmin;
    }

    public boolean isShowSkip() {
        return showSkip;
    }

    public void setShowSkip(boolean showSkip) {
        this.showSkip = showSkip;
    }

    public boolean isShowLanguageSelector() {
        return showLanguageSelector;
    }

    public void setShowLanguageSelector(boolean showLanguageSelector) {
        this.showLanguageSelector = showLanguageSelector;
    }
}
