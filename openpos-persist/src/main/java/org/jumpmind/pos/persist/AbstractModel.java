package org.jumpmind.pos.persist;

import java.io.Serializable;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.jumpmind.pos.persist.model.IAuditableModel;
import org.jumpmind.pos.util.ClassUtils;


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

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "__extensionClass")
    private Map<Class, Object> extensions = new HashMap<>();

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

    public void addExtension(Class clazz, Object extension){
        extensions.put(clazz, extension);
    }

    public Map<Class, Object> getExtensions() { return new HashMap<>(extensions); }

    public <T> T getExtension(Class<T> clazz){
        if(!extensions.containsKey(clazz)){
            try {
                extensions.put(clazz, clazz.newInstance());
            } catch (Exception e) {
               throw new PersistException("Error getting extension class " + clazz, e);
            }
        }
        return (T)extensions.get(clazz);
    }

    public <T> T getExtension(String className) {
        Class<T> clazz = ClassUtils.loadClass(className);
        return getExtension(clazz);
    }
}
