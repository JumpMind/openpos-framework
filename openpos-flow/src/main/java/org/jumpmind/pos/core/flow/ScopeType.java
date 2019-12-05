package org.jumpmind.pos.core.flow;


public enum ScopeType {

    State(10), // Created new each time before arriving at a state.
    Config(20), // Static, configuration time scope usually defined in the flow config of flow yaml.
    Device(50), // Value that lives for the entire lifetime of a given node/device.
    Session(40), // Values that have the same lifecycle as the logged in user session. 
    Conversation(30), // Values that follow a lifecycle similar to a transaction, order, etc.
    Flow(20) // // Values that live for the duration of a given flow (ie, several states composing a substate).  Substates inherit their parents flow values, but new values in substates do not trickle up to the parent flow.
 
    ;
    
    private int rank;
    private ScopeType(int rank) {
        this.rank = rank;
    }
    
    /**
     * Returns the relative rank of this ScopeType with respect to the other ScopeTypes.
     * Beans with lower ScopeType rank values can contain beans with the same or higher
     * ScopeType rank values.
     * @return An integer value representing the relative rank of the ScopeType.
     */
    public int rank() {
        return this.rank;
    }
}
