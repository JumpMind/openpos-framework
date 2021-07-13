package org.jumpmind.pos.core.ui.messagepart;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IHasBackButton;

import java.io.Serializable;

public class SelfCheckoutMenuPart implements IHasBackButton, Serializable {

    private static final long serialVersionUID = 1L;

    private String deviceId;
    private String operatorText;
    private String headerText;
    private String headerIcon;
    private ActionItem backButton;
    private ActionItem lightButton;
    private ActionItem skipButton = new ActionItem("key:selfcheckout:button.skip", "Skip", false);
    private boolean showScan;
    private boolean showAdmin;
    private boolean showLanguageSelector = false;
    private String logo;

    public SelfCheckoutMenuPart() {
        logo = "content:home-screen-logo";
    }

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

    public ActionItem getSkipButton() {
        return skipButton;
    }

    public void setSkipButton(ActionItem skipButton) {
        this.skipButton = skipButton;
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

    public void setShowSkip(boolean showSkip) {
        this.skipButton.setEnabled(showSkip);
    }

    public boolean isShowLanguageSelector() {
        return showLanguageSelector;
    }

    public void setShowLanguageSelector(boolean showLanguageSelector) {
        this.showLanguageSelector = showLanguageSelector;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    public ActionItem getLightButton() {
        return this.lightButton;
    }
    
    public void setLightButton(ActionItem lightButton) {
        this.lightButton = lightButton;
    }
}
