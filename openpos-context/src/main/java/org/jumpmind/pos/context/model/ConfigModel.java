package org.jumpmind.pos.context.model;

import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.persist.Column;
import org.jumpmind.pos.persist.Entity;
import org.jumpmind.pos.persist.Table;

@Table(name="config")
public class ConfigModel extends Entity {
    
    @Column(primaryKey=true)
    private Date effectiveDate;
    @Column(primaryKey=true)
    private String configName;
    @Column
    private Date expirationDate;
    @Column
    private boolean enabledFlag;
    // The value is a JSON object.
    @Column(type=Types.CLOB)
    private String configValue;
    
    private Map<String, String> tags = new HashMap<String, String>();

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isEnabledFlag() {
        return enabledFlag;
    }

    public void setEnabledFlag(boolean enabledFlag) {
        this.enabledFlag = enabledFlag;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public Map<String, String> getTags() {
        return tags;
    }

    public void setTags(Map<String, String> tags) {
        this.tags = tags;
    }
 

}
