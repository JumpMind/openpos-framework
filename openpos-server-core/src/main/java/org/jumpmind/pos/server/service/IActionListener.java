package org.jumpmind.pos.server.service;

import java.util.Collection;

import org.jumpmind.pos.server.model.Action;

public interface IActionListener {

    Collection<String> getRegisteredTypes();
    
    void actionOccurred(String deviceId, Action action);
    
}
