package org.jumpmind.pos.app.state.user;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.Action;
import org.jumpmind.pos.core.flow.IState;
import org.jumpmind.pos.core.flow.IStateInterceptor;
import org.jumpmind.pos.core.flow.StateManager;
import org.jumpmind.pos.user.model.User;
import org.springframework.stereotype.Component;

@Component
public class SecurityStateInterceptor implements IStateInterceptor {

    @Override
    public IState intercept(StateManager stateManager, IState currentState, IState newState, Action action) {
        String privilegeRequired = getPriviledgeRequired(newState);
        if (StringUtils.isEmpty(privilegeRequired)) {
            return null;
        }
        
        User user = stateManager.getScopeValue("currentUser"); 
        
        if (user == null) {            
            return new UserLoginState(currentState, newState);
        } else {
            return null;
        }
    }

    private String getPriviledgeRequired(IState newState) {
        if (newState.toString().contains("SellState")) { // TODO should come from config
            return "SELL";
        }
        
        return null;
        
    }
}
