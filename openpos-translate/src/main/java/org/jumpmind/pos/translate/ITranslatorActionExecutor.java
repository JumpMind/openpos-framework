package org.jumpmind.pos.translate;

import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.server.model.Action;

public interface ITranslatorActionExecutor {

    public boolean canExecute(Object translator, Action action);
    public void executeAction(Object translator, IStateManager stateManager, Action action, Object...args);
}
