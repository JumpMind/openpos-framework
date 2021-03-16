package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import java.util.*;

import lombok.Data;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.model.AugmenterConfig;

import javax.annotation.sql.DataSourceDefinition;

@Data
public class ModelClassMetaData {

    private Table table;
    private Table shadowTable;
    private Class<?> clazz;
    private List<Class<?>> extensionClazzes;
    private String idxPrefix;
    private Map<String, FieldMetaData> entityIdFieldMetaDatas = new LinkedHashMap<>();
    private Map<String, FieldMetaData> entityFieldMetaDatas = new LinkedHashMap<>();
    private List<Column> primaryKeyColumns = new ArrayList<Column>();
    private Set<String> primaryKeyFieldNames = new LinkedHashSet<>();
    private Set<String> augmentedFieldNames = new LinkedHashSet<>();
    private List<AugmenterConfig> augmenterConfigs = new ArrayList<>();

    private String shadowPrefix;
    private String modulePrefix;

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

    public boolean isPrimaryKey(Field field) {
        return isPrimaryKey(field.getName());
    }

    public boolean isPrimaryKey(String fieldName) {
        return primaryKeyFieldNames.contains(fieldName);
    }

    public void init() {
        initPrimaryKeyColumns();
    }

    private void initPrimaryKeyColumns() {
        int primaryKeySequence = 0;
        for (String primaryKeyFieldName : primaryKeyFieldNames) {
            FieldMetaData fieldMetaData = entityIdFieldMetaDatas.get(primaryKeyFieldName);
            if (fieldMetaData == null) {
                throw new PersistException("There is no field meta data for field name '" + primaryKeyFieldName + "' for model " + getClazz().getSimpleName());
            }

            // fieldMetaData.getColumn().setPrimaryKeySequence(primaryKeySequence++);
            primaryKeyColumns.add(fieldMetaData.getColumn());
        }
    }

    public Table getTableForDeviceMode(String deviceMode)  {
        return (deviceMode.equalsIgnoreCase("training") && hasShadowTable() ? getShadowTable() : getTable());
    }

    public void setShadowTable(String shadowPrefix, String modulePrefix)  {
        this.shadowTable = table.copy();
        this.shadowPrefix = shadowPrefix;
        this.modulePrefix = modulePrefix;
    }

    public boolean hasShadowTable()  {
        return (shadowTable != null);
    }

    public String getShadowTableName()  {
        return (hasShadowTable() ? shadowTable.getName() : table.getName());
    }

    @Override
    public String toString() {
        return clazz.getName();
    }
}
