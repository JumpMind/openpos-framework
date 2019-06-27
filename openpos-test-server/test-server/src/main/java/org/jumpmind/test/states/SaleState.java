package org.jumpmind.test.states;

import org.jumpmind.pos.core.flow.*;
import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.IconType;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.message.HomeUIMessage;
import org.jumpmind.pos.core.ui.message.SaleUIMessage;
import org.jumpmind.pos.core.ui.messagepart.BaconStripPart;
import org.jumpmind.pos.core.ui.messagepart.MessagePartConstants;
import org.jumpmind.pos.server.model.Action;

import java.util.ArrayList;
import java.util.List;

public class SaleState {

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @OnArrive
    public void arrive(Action action) {
        stateManager.showScreen(buildScreen());
    }

    private UIMessage buildScreen(){
        SaleUIMessage message = new SaleUIMessage();

        message.setCheckoutButton(new ActionItem("checkout", "checkout"));

        BaconStripPart bacon = new BaconStripPart();
        bacon.setHeaderIcon(IconType.Home);
        bacon.setHeaderText("Loading Screen Test");

        message.addMessagePart(MessagePartConstants.BaconStrip, bacon);

        List<ActionItem> links = new ArrayList<>();

        links.add(new ActionItem("ShowDialog", "ShowDialog"));

        message.addMessagePart(MessagePartConstants.SausageLinks, links);

        return message;
    }

    @ActionHandler
    public void onShowDialog(Action action) throws InterruptedException {
        Thread.sleep(5000);

        stateManager.doAction("ShowDialog");
    }

}
