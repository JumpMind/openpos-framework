package org.jumpmind.pos.service;

public interface Module {

    public String getName();
    
    public String getVersion();
    
    public default void start() {
    }
    
    public String getTablePrefix();
    
}
