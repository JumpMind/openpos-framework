package org.jumpmind.pos.core.flow;

import java.util.HashMap;
import java.util.Map;

public class InterceptedStateContext extends StateContext {
    
    private Map<Class<? extends Object>, Object> terminatingStateToNextStateMapping = new HashMap<>();

    public void mapTerminatingState(Class<? extends Object> terminingState, IState proceedsToState) {
        terminatingStateToNextStateMapping.put(terminingState, proceedsToState);
    }

    public Object getProceedToStateForTerminatingState(Class<? extends IState> targetStateClass) {
        return terminatingStateToNextStateMapping.get(targetStateClass);
    }
    
}
