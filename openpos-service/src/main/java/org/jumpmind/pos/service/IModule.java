package org.jumpmind.pos.service;

import org.jumpmind.db.model.Table;

import java.util.List;

public interface IModule {

    public String getName();
    
    public String getVersion();
    
    public default void initialize() {
    }
    
    public default void start() {
        
    }
    
    public void exportData(String format, String dir, boolean includeModuleTables, String whereClause, List<String> tableFilter, String batchId);

    public String getTablePrefix();

    String getURL();

    String getDriver();

    void rebuildDatabase();
    
}
