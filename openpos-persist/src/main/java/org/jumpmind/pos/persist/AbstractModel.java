package org.jumpmind.pos.persist;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jumpmind.pos.persist.model.IAuditableModel;


public abstract class AbstractModel implements IAuditableModel, Serializable {

    private static final long serialVersionUID = 1L;

    @ColumnDef(required=true, defaultValue = "CURRENT_TIMESTAMP",
            description="Timestamp when this entry was created.")
    private Date createTime = new Date();

    @ColumnDef(required=true, size="50", defaultValue = "system",
            description="The user who last updated this entry.")    
    private String createBy;

    @ColumnDef(defaultValue = "CURRENT_TIMESTAMP", description="Timestamp when a user last updated this entry.")
    private Date lastUpdateTime = new Date();

    @ColumnDef(required=true, size="50", defaultValue = "system",
            description="The user who last updated this entry.")    
    private String lastUpdateBy;

    @JsonIgnore
    private Map<String, Object> systemData = new HashMap<>();

    @JsonIgnore
    private Map<String, Object> additionalFields = new CaseInsensitiveMap<String, Object>();

    @Override
    public Date getCreateTime() {
        return createTime;
    }

    @Override
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String getCreateBy() {
        return createBy;
    }

    @Override
    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    @Override
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    @Override
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    @Override
    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
    
    public void setAdditionalField(String fieldName, Object fieldValue) {
        additionalFields.put(fieldName, fieldValue);
    }    
    
    public Object getAdditionalField(String fieldName) {
        return additionalFields.get(fieldName);
    }
    
    public Map<String, Object> getAdditionalFields() {
        return new HashMap<>(additionalFields);
    }    
    
    
}
