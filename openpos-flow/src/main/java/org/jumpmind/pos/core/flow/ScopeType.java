package org.jumpmind.pos.core.flow;


public enum ScopeType {

    State, // Created new each time before arriving at a state.
    Config, // Static, configuration time scope usually defined in the flow config of flow yaml.
    Device, // Value that lives for the entire lifetime of a given node/device.
    Session, // Values that have the same lifecycle as the logged in user session. 
    Conversation, // Values that follow a lifecycle similar to a transaction, order, etc.
    Flow // // Values that live for the duration of a given flow (ie, several states composing a substate).  Substates inherit their parents flow values, but new values in substates do not trickle up to the parent flow.
    
}
