package org.jumpmind.pos.service.model;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table
public class ModuleInfo extends Entity {

    @Column(primaryKey=true) 
    String installationId;
    
    @Column
    String currentVersion;
    
    public ModuleInfo() {
    }
    
    public ModuleInfo(String installationId, String currentVersion) {
        this.installationId = installationId;
        this.currentVersion = currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }
    
    public String getCurrentVersion() {
        return currentVersion;
    }
    
    public void setInstallationId(String installationId) {
        this.installationId = installationId;
    }
    
    public String getInstallationId() {
        return installationId;
    }
}
