package org.jumpmind.pos.persist;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.collections4.map.CaseInsensitiveMap;


public abstract class Entity {
    
    @Column(required=true,
            description="Timestamp when this entry was created.")
    private Date createTime = new Date();

    @Column(required=true, size="50",
            description="The user who last updated this entry.")    
    private String createBy;

    @Column(description="Timestamp when a user last updated this entry.")
    private Date lastUpdateTime = new Date();

    @Column(required=true, size="50",
            description="The user who last updated this entry.")    
    private String lastUpdateBy;
    
    @SuppressWarnings("unused")
    private transient Map<String, Object> systemData = new HashMap<>(); 
    
    private transient Map<String, Object> additionalFields = new CaseInsensitiveMap<String, Object>();

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

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
