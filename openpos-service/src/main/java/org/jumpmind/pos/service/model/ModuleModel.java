package org.jumpmind.pos.service.model;

import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name="module", description="This table is used to track runtime information about the module.")
public class ModuleModel extends AbstractModel {

    private static final long serialVersionUID = 1L;

    @ColumnDef(primaryKey=true) 
    String installationId;
    
    @ColumnDef
    String currentVersion;
    
    public ModuleModel() {
    }
    
    public ModuleModel(String installationId, String currentVersion) {
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
