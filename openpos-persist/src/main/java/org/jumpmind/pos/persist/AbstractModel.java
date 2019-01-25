package org.jumpmind.pos.persist;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jumpmind.pos.persist.model.IAuditableModel;


public abstract class AbstractModel implements IAuditableModel {

    @ColumnDef(required=true,
            description="Timestamp when this entry was created.")
    private Date createTime = new Date();

    @ColumnDef(required=true, size="50",
            description="The user who last updated this entry.")    
    private String createBy;

    @ColumnDef(description="Timestamp when a user last updated this entry.")
    private Date lastUpdateTime = new Date();

    @ColumnDef(required=true, size="50",
            description="The user who last updated this entry.")    
    private String lastUpdateBy;
    
    @SuppressWarnings("unused")
    private transient Map<String, Object> systemData = new HashMap<>(); 
    
    private transient Map<String, Object> additionalFields = new CaseInsensitiveMap<String, Object>();

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
