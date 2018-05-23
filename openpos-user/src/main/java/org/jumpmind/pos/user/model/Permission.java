package org.jumpmind.pos.user.model;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(description = "A table that associates workgroups with resources and specifies their access level.")
public class Permission extends Entity {

    @Column(primaryKey=true,
            description = "e.g. sell.apply.discount OR sell.*")
    private String permissionId;
    
    @Column(description = "Allow an operation such as a manager override.")
    private boolean allowsOverride;

    public String getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(String permissionId) {
        this.permissionId = permissionId;
    }

    public boolean isAllowsOverride() {
        return allowsOverride;
    }

    public void setAllowsOverride(boolean allowsOverride) {
        this.allowsOverride = allowsOverride;
    }

}
