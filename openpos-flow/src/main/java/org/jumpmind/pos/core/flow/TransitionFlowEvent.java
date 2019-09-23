package org.jumpmind.pos.core.flow;

import org.jumpmind.pos.server.model.Action;

public class TransitionFlowEvent extends FlowEvent {

    Action action;

    String oldStateName;

    String newStateName;

    public TransitionFlowEvent() {
    }

    public TransitionFlowEvent(String deviceId, String appId, Action action, String oldStateName, String newStateName) {
        this.appId = appId;
        this.deviceId = deviceId;
        this.action = action;
        this.oldStateName = oldStateName;
        this.newStateName = newStateName;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getOldStateName() {
        return oldStateName;
    }

    public void setOldStateName(String oldStateName) {
        this.oldStateName = oldStateName;
    }

    public String getNewStateName() {
        return newStateName;
    }

    public void setNewStateName(String newStateName) {
        this.newStateName = newStateName;
    }

}
