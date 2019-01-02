package org.jumpmind.pos.service;

public interface IModule {

    public String getName();
    
    public String getVersion();
    
    public default void start() {
    }
    
    public void exportData(String format, String dir, boolean includeModuleTables);
    
    public String getTablePrefix();

    String getURL();

    String getDriver();
    
}
