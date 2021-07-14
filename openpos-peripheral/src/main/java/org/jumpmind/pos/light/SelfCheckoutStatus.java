package org.jumpmind.pos.light;

public enum SelfCheckoutStatus {

    HOME("home"),
    SCANNING("scanning"),
    TENDERING("tendering"),
    HELP_REQUESTED("helpRequested"),
    ADMIN("admin");

    public final String label;

    private SelfCheckoutStatus(String label) {
        this.label = label;
    }
    
}
