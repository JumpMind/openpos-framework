package org.jumpmind.pos.tax.model;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(description = "Tax Jurisdiction")
public class Jurisdiction extends Entity {

    @Column(primaryKey = true)
    private String id;

    @Column
    private String geoCode;

    @Column
    private String authorityId;

    private Authority authority;

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setGeoCode(String geoCode) {
        this.geoCode = geoCode;
    }

    public String getGeoCode() {
        return geoCode;
    }

    public void setAuthority(Authority authority) {
        this.authority = authority;
    }

    public Authority getAuthority() {
        return authority;
    }

    public void setAuthorityId(String authorityId) {
        this.authorityId = authorityId;
    }

    public String getAuthorityId() {
        return authorityId;
    }

}
