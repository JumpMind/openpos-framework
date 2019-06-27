package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.db.model.Table;

public class ModelClassMetaData {

    private Table table;
    private Class<?> clazz;
    private Map<String, Field> entityIdFields = new HashMap<>();
    private Map<String, Field> entityFields = new HashMap<>();
    
    public ModelClassMetaData() {
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Map<String, Field> getEntityIdFields() {
        return entityIdFields;
    }
    
    public Field getField(String name) {
        return entityFields.get(name);
    }

    public Map<String, Field> getEntityFields() {
        return entityFields;
    }
    
    public Class<?> getClazz() {
        return clazz;
    }
    
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void addEntityIdField(String name, Field field) {
        entityIdFields.put(name, field);
    }

    public void addEntityField(String name, Field field) {
        entityFields.put(name, field);
    }
}
