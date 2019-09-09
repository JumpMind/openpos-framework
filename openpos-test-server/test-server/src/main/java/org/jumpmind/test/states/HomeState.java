package org.jumpmind.test.states;

import org.jumpmind.pos.core.flow.*;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.IconType;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.message.HomeUIMessage;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.jumpmind.pos.server.model.Action;

public class HomeState {

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @OnArrive
    public void arrive(Action action) {
        stateManager.showScreen(buildScreen());
    }

    private UIMessage buildScreen(){
        HomeUIMessage message = new HomeUIMessage();
        BaconStripPart bacon = new BaconStripPart();
        bacon.setHeaderIcon(IconType.Home);
        bacon.setHeaderText("Loading Screen Test");

        message.addMessagePart(MessagePartConstants.BaconStrip, bacon);

        for( int i = 0; i < 12; ++i ) {
            message.addMenuItem( new ActionItem("F"+i, "Next", IconType.Email));
        }

        return message;
    }

    @ActionHandler
    public void onF1(Action action) throws InterruptedException {
        Thread.sleep(5000);

        stateManager.doAction("Next");
    }
}
