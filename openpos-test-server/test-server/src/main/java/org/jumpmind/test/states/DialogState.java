package org.jumpmind.test.states;

import org.jumpmind.pos.core.flow.*;
import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.core.ui.message.DialogUIMessage;
import org.jumpmind.pos.server.model.Action;

public class DialogState {

    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @OnArrive
    public void arrive(Action action) {
        stateManager.showScreen(buildScreen());
    }

    private UIMessage buildScreen() {
        DialogUIMessage dialog = new DialogUIMessage();
        dialog.setMessage("Test Message");
        dialog.addButton( new ActionItem("Next", "Next"));
        dialog.addButton( new ActionItem( "Refresh", "Refresh"));
        dialog.addButton( new ActionItem("Close", "Close"));
        dialog.setTitle("Dialog Title");
        return dialog;
    }

    @ActionHandler
    public void onNext(Action action) throws InterruptedException {
        Thread.sleep(5000);
        stateManager.doAction(action);
    }

    @ActionHandler
    public void onRefresh(Action action) {
        stateManager.refreshScreen();
    }
}
