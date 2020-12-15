package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.util.clientcontext.ClientContext;

public interface IClientContextUpdater {

    public void update(ClientContext clientContext, IStateManager stateManager);

}
