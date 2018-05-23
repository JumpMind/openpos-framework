package org.jumpmind.pos.user.model;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table
public class WorkgroupPermission extends Entity {

    @Column(primaryKey=true,
            size = "16")
    private String workgroupRowId;
    
    @Column(primaryKey=true,
            size = "16")
    private String permissionRowId;

    public String getWorkgroupRowId() {
        return workgroupRowId;
    }

    public void setWorkgroupRowId(String workgroupRowId) {
        this.workgroupRowId = workgroupRowId;
    }

    public String getPermissionRowId() {
        return permissionRowId;
    }

    public void setPermissionRowId(String permissionRowId) {
        this.permissionRowId = permissionRowId;
    }
    
}
