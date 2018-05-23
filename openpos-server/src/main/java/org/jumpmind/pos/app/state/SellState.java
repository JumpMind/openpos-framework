package org.jumpmind.pos.app.state;

import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.flow.ActionHandler;
import org.jumpmind.pos.core.flow.IState;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.core.screen.MenuItem;
import org.jumpmind.pos.core.screen.SellItemScreen;
import org.jumpmind.pos.core.template.SellTemplate;

public class SellState implements IState {

    @In(scope=ScopeType.Node)
    IStateManager stateManager;
    
    @Override
    public void arrive(Action action) {
        stateManager.showScreen(buildScreen());
    }

    protected SellItemScreen buildScreen() {
        SellItemScreen screen = new SellItemScreen();
        screen.setLogoutButton(new MenuItem("Back", "Logout", "exit_to_app"));
        screen.setPrompt("Ready to begin");
        screen.setName("Sell");        
        SellTemplate template = new SellTemplate();
        template.addLocalMenuItem(new MenuItem("CustomerSearch", "Customer", "person"));
        template.addLocalMenuItem(new MenuItem("Returns", "Returns", "receipt"));
        template.addLocalMenuItem(new MenuItem("Foo", "Invalid Action", "receipt"));
        screen.setTemplate(template);        
        return screen;
    }    
    
    @ActionHandler    
    protected void onCustomerSearchComplete(Action action) {
        stateManager.getUI().notify("Got the customer selected " + action.getData(), "CustomerSelectAcknowledged");
    }
    
    @ActionHandler
    protected void onCustomerSelectAcknowledged(Action action) {
        stateManager.showScreen(buildScreen());        
    }
}
