package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.util.*;

import lombok.Data;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;

import javax.annotation.sql.DataSourceDefinition;

@Data
public class ModelClassMetaData {

    private Table table;
    private Class<?> clazz;
    private List<Class<?>> extensionClazzes;
    private String idxPrefix;
    private Map<String, FieldMetaData> entityIdFieldMetaDatas = new LinkedHashMap<>();
    private Map<String, FieldMetaData> entityFieldMetaDatas = new LinkedHashMap<>();
    private List<Column> primaryKeyColumns = new ArrayList<Column>();
    private Set<String> primaryKeyFieldNames = new LinkedHashSet<>();

    public ModelClassMetaData() {
    }

    public FieldMetaData getFieldMetaData(String name) {
        return entityFieldMetaDatas.get(name);
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

    public boolean isPrimaryKey(Field field) {
        return isPrimaryKey(field.getName());
    }

    public boolean isPrimaryKey(String fieldName) {
        return primaryKeyFieldNames.contains(fieldName);
    }

    @Override
    public String toString() {
        return clazz.getName();
    }
}
