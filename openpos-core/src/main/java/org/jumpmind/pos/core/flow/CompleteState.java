package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.server.model.Action;

public class CompleteState {

    @OnArrive
    public void arrive(Action action) {
        // No op.
    }

}
