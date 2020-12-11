package org.jumpmind.pos.core.ui.message;

import org.jumpmind.pos.core.ui.UIMessage;

public class DashboardUIMessage extends UIMessage {
    private static final long serialVersionUID = 1L;

    public DashboardUIMessage() {
        this.setScreenType(UIMessageType.DASHBOARD);
        this.setId("dashboard");
    }
}
