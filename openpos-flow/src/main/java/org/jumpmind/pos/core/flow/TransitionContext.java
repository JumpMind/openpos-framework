package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.server.model.Action;

public class TransitionContext {
    private Action originalAction;
    private StateContext sourceStateContext;

    public TransitionContext(Action originalAction, StateContext sourceStateContext) {
        this.originalAction = originalAction;
        this.sourceStateContext = sourceStateContext;
    }

    public Action getOriginalAction() {
        return originalAction;
    }

    public void setOriginalAction(Action originalAction) {
        this.originalAction = originalAction;
    }

    public StateContext getSourceStateContext() {
        return sourceStateContext;
    }

    public void setSourceStateContext(StateContext sourceStateContext) {
        this.sourceStateContext = sourceStateContext;
    }
}
