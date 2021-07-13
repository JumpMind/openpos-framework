package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;

public interface IStateManagerObserver {

    public default void onTransition(ApplicationState applicationState, Transition transition,
                Action action, String returnActionName) {

    }

    public default void onAction(ApplicationState applicationState, Action action) {

    }

    public default void onScreen(ApplicationState applicationState, UIMessage screen) {

    }

    public default void onTransitionStep(ApplicationState applicationState, Transition transition, ITransitionStep currentTransitionStep) {

    }

}
