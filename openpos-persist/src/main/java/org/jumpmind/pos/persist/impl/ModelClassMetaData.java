package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;

public class ModelClassMetaData {

    private Table table;
    private Class<?> clazz;
    private String idxPrefix;
    private Map<String, FieldMetaData> entityIdFieldMetaDatas = new HashMap<>();
    private Map<String, FieldMetaData> entityFieldMetaDatas = new HashMap<>();
    private List<Column> primaryKeyColumns = new ArrayList<Column>();
    
    public ModelClassMetaData() {
    }

    public Table getTable() {
        return table;
    }

    public List<Column> getPrimaryKeyColumns() { return primaryKeyColumns; }

    public void setTable(Table table) {
        this.table = table;
    }

    public Map<String, FieldMetaData> getEntityIdFieldMetaDatas() {
        return entityIdFieldMetaDatas;
    }
    
    public FieldMetaData getFieldMetaData(String name) {
        return entityFieldMetaDatas.get(name);
    }

    public Map<String, FieldMetaData> getEntityFieldMetaDatas() {
        return entityFieldMetaDatas;
    }

    public Class<?> getClazz() {
        return clazz;
    }
    
    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public void addEntityIdFieldMetadata(String name, FieldMetaData fieldMetaData) {
        entityIdFieldMetaDatas.put(name, fieldMetaData);
    }

    public void addEntityFieldMetaData(String name, FieldMetaData fieldMetaData) {
        entityFieldMetaDatas.put(name, fieldMetaData);
    }

    public void addPrimaryKeyColumn(Column column) {
        primaryKeyColumns.add(column);
    }

    public String getIdxPrefix() {return idxPrefix;}

    public void setIdxPrefix(String idxPrefix) { this.idxPrefix = idxPrefix; }

    @Override
    public String toString() {
        return clazz.getName();
    }
}
