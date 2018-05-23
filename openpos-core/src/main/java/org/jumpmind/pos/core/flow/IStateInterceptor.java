package org.jumpmind.pos.core.flow;


public interface IStateInterceptor {

    public IState intercept(StateManager stateManager, IState currentState, IState newState, Action action);
    
    public default int getPriority() {
        return 0;
    }
}
