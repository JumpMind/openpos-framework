package org.jumpmind.pos.core.model;

public final class MessageType {

    private MessageType() {
    }

    public static final String Screen = "Screen";
    public static final String Dialog = "Dialog";
    public static final String Toast = "Toast";
    public static final String CloseToast = "CloseToast";
    public static final String DevTools = "DevTools";
    public static final String Loading = "Loading";
    public static final String ConfigChanged = "ConfigChanged";
    public static final String Proxy = "Proxy";
    public static final String UIData = "UIData";
    public static final String LocaleChanged = "LocaleChanged";
    public static final String Connected = "Connected";
    public static final String SingleSignOnRequest = "SingleSignOnRequest";
    public static final String LockScreen = "LockScreen";
    public static final String UnlockScreen = "UnlockScreen";
    public static final String Startup = "Startup";
    public static final String Scan = "Scan";
    public static final String SimulatedPeripheral = "SimulatedPeripheral";
    public static final String Watermark = "Watermark";
    public static final String HideWatermark = "HideWatermark";
    public static final String StatusBar = "StatusBar";
    public static final String HideStatusBar = "HideStatusBar";
    public static final String ClientExecutable = "ClientExecutable";
    public static final String PeripheralDeviceStateChanged = "PeripheralDeviceStateChanged";
}
