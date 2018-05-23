package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.log4j.Logger;
import org.jumpmind.pos.persist.Entity;

public class EntitySystemInfo {
    
    private static Logger log = Logger.getLogger(EntitySystemInfo.class);
    private static final boolean FORCE_ACCESS = true;
    
    public static final String ENTITY_RETRIEVAL_TIME = "entity.retrieval.time";
    
    private Map<String, Object> systemData;

    @SuppressWarnings("unchecked")
    public EntitySystemInfo(Entity entity) {
        Field field = FieldUtils.getField(entity.getClass(), "systemInfo", FORCE_ACCESS);
        try {
            systemData = (Map<String, Object>) field.get(entity);
        } catch (Exception ex) {
            log.debug("Failed to access hidden system field systemInfo on object: " + entity, ex);
            systemData = new HashMap<>();
        } 
    }
    
    public void put(String key, Object value) {
        systemData.put(key, value);
    }
    
    public Object get(String key) {
        return systemData.get(key);
    }
    
    public Set<String> keySet() {
        return systemData.keySet();
    }
    public boolean isNew() {
        return ! systemData.containsKey(ENTITY_RETRIEVAL_TIME);
    }
    public void setRetrievalTime(Date retrievalTime) {
        systemData.put(ENTITY_RETRIEVAL_TIME, retrievalTime);
    }
    public Date getRetrievalTime() {
        return (Date) systemData.get(ENTITY_RETRIEVAL_TIME);
    }
    
}
