package org.jumpmind.pos.core.flow;


public enum ScopeType {

    Config, // Static, configuration time scope.
    Node, // Value that lives for the entire lifetime of a given node.
    Session, // Values that have the same lifecycle as the logged in user session. 
    Conversation, // Values that of a lifecycle similar to a transaction.
    Flow // Values that live for the duration of a given flow (ie, several states composing a substate). 
    
}
