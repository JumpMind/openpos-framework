package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class ClientConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean mimicScroll = false;
    private boolean useOnScreenKeyboard = false;
    private boolean useTouchListener = true;
    private boolean useSavePoints = true;
    private boolean showRegisterStatus = false;
    private boolean clickableRegisterStatus = false;
    private boolean offlineOnlyRegisterStatus = false;
    private int keepAliveMillis = 30000;
    private int maxSignaturePoints = -1;
    private long maxResponseSizeBytes = 79500;
    private boolean enableAutocomplete = false;
    private boolean enableMenuClose = true;
    private boolean enableKeybinds = false;

    public ClientConfiguration() {
    }

    public boolean isMimicScroll() {
        return mimicScroll;
    }

    public void setMimicScroll(boolean mimicScroll) {
        this.mimicScroll = mimicScroll;
    }

    public boolean isUseOnScreenKeyboard() {
        return useOnScreenKeyboard;
    }

    public void setUseOnScreenKeyboard(boolean useOnScreenKeyboard) {
        this.useOnScreenKeyboard = useOnScreenKeyboard;
    }

    public boolean isUseTouchListener() {
        return useTouchListener;
    }

    public void setUseTouchListener(boolean useTouchListener) {
        this.useTouchListener = useTouchListener;
    }

    public boolean isUseSavePoints() {
        return useSavePoints;
    }

    public void setUseSavePoints(boolean useSavePoints) {
        this.useSavePoints = useSavePoints;
    }

    public boolean isShowRegisterStatus() {
        return showRegisterStatus;
    }

    public void setShowRegisterStatus(boolean showRegisterStatus) {
        this.showRegisterStatus = showRegisterStatus;
    }

    public boolean isClickableRegisterStatus() {
        return clickableRegisterStatus;
    }

    public void setClickableRegisterStatus(boolean clickableRegisterStatus) {
        this.clickableRegisterStatus = clickableRegisterStatus;
    }

    public boolean isOfflineOnlyRegisterStatus() {
        return offlineOnlyRegisterStatus;
    }

    public void setOfflineOnlyRegisterStatus(boolean offlineOnlyRegisterStatus) {
        this.offlineOnlyRegisterStatus = offlineOnlyRegisterStatus;
    }

    public int getKeepAliveMillis() {
        return keepAliveMillis;
    }

    public void setKeepAliveMillis(int keepAliveMillis) {
        this.keepAliveMillis = keepAliveMillis;
    }

    public int getMaxSignaturePoints() {
        return maxSignaturePoints;
    }

    public void setMaxSignaturePoints(int maxSignaturePoints) {
        this.maxSignaturePoints = maxSignaturePoints;
    }

    public long getMaxResponseSizeBytes() {
        return maxResponseSizeBytes;
    }

    public void setMaxResponseSizeBytes(long maxResponseSizeBytes) {
        this.maxResponseSizeBytes = maxResponseSizeBytes;
    }

    public boolean isEnableAutocomplete() {
        return enableAutocomplete;
    }

    public void setEnableAutocomplete(boolean enableAutocomplete) {
        this.enableAutocomplete = enableAutocomplete;
    }

    public boolean isEnableMenuClose() {
        return enableMenuClose;
    }

    public void setEnableMenuClose(boolean enableMenuClose) {
        this.enableMenuClose = enableMenuClose;
    }

    public boolean isEnableKeybinds() {
        return enableKeybinds;
    }

    public void setEnableKeybinds(boolean enableKeybinds) {
        this.enableKeybinds = enableKeybinds;
    }

}
