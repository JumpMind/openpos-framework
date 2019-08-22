package org.jumpmind.test.states;

import org.jumpmind.pos.core.flow.*;
import org.jumpmind.pos.core.screen.*;
import org.jumpmind.pos.core.template.Scan;
import org.jumpmind.pos.core.template.SellTemplate;
import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;


public class SaleState {
    @In(scope = ScopeType.Device)
    IStateManager stateManager;

    @OnArrive
    public void arrive(Action action) {
        stateManager.showScreen(buildScreen());
    }

    private UIMessage buildScreen(){
        SellTemplate template = new SellTemplate();

        SellItemScreen screen = new SellItemScreen();
        screen.setTemplate(template);
        template.setScan(new Scan());


        return screen;
    }

    @ActionHandler
    public void onF1(Action action) throws InterruptedException {
        stateManager.doAction("Next");
    }
}