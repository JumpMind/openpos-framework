package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.core.ui.UIMessage;
import org.jumpmind.pos.server.model.Action;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StateManagerObservers {

    @Autowired(required = false)
    List<? extends IStateManagerObserver> stateManagerObservers;

    public void onTransition(ApplicationState applicationState, Transition transition, Action action, String returnActionName) {
        if (stateManagerObservers != null) {
            for (IStateManagerObserver stateManagerObserver : stateManagerObservers) {
                stateManagerObserver.onTransition(applicationState, transition, action, returnActionName);
            }
        }
    }

    public void onAction(ApplicationState applicationState, Action action) {
        if (stateManagerObservers != null) {
            for (IStateManagerObserver stateManagerObserver : stateManagerObservers) {
                stateManagerObserver.onAction(applicationState, action);
            }
        }
    }

    public void onScreen(ApplicationState applicationState, UIMessage screen) {
        if (stateManagerObservers != null) {
            for (IStateManagerObserver stateManagerObserver : stateManagerObservers) {
                stateManagerObserver.onScreen(applicationState, screen);
            }
        }
    }

    public void onTransition(ApplicationState applicationState, Transition transition, ITransitionStep currentTransitionStep) {
        if (stateManagerObservers != null) {
            for (IStateManagerObserver stateManagerObserver : stateManagerObservers) {
                stateManagerObserver.onTransitionStep(applicationState, transition, currentTransitionStep);
            }
        }
    }
}
