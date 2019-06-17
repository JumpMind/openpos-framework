package org.jumpmind.pos.core.screen;

public class PaymentStatusScreen extends Screen {

    private static final long serialVersionUID = 1L;
    
    private String pinPadStatus;
    
    public String getPinPadStatus() {
        return pinPadStatus;
    }

    public void setPinPadStatus(String pinPadStatus) {
        this.pinPadStatus = pinPadStatus;
    }

    public PaymentStatusScreen() {
        setScreenType(ScreenType.PaymentStatus);
    }
}