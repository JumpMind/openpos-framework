package org.jumpmind.pos.user.model;

import java.util.List;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(name = "workgroup",
       description = "A group within a workforce that normally works together and has similar access control permissions.")
public class Workgroup extends Entity {
    
    @Column(primaryKey=true)
    private String workgroupId;

    @Column(size = "255",
            description = "A description for the Workgroup.")
    private String description;
    
    private List<Permission> permissions;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
