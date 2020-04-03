package org.jumpmind.pos.persist.impl;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ModelMetaData {

    private List<ModelClassMetaData> modelClassMetaData;
    private Field systemData;


    public List<ModelClassMetaData> getModelClassMetaData() {
        return modelClassMetaData;
    }

    public void setModelClassMetaData(List<ModelClassMetaData> modelClassMetaData) {
        this.modelClassMetaData = modelClassMetaData;
    }

    public Field getSystemDataField() {
        return systemData;
    }

    public void setSystemDataField(Field systemInfoField) {
        this.systemData = systemInfoField;
    }

    public void init() {
        systemData = FieldUtils.getField(modelClassMetaData.get(modelClassMetaData.size()-1).getClazz(), "systemData", true);
    }

    /**
     * Returns either the column name associated with the property on the given class or the column name of the first
     * matching property found in superclasses.  The column name associated with the property on the given class
     * has highest priority.
     * @param clazz The Class to search
     * @param propertyName The bean property name to search for
     * @return The column name associated with the given property.
     */
    public <T> String getColumnNameForProperty(Class<T> clazz, String propertyName) {
        String columnName = null;
        if (this.modelClassMetaData != null) {
            for (ModelClassMetaData m : this.modelClassMetaData) {
                Map<String, FieldMetaData> entityFieldMetaDatas = m.getEntityFieldMetaDatas();
                FieldMetaData d = entityFieldMetaDatas.get(propertyName);
                if (d != null) {
                    if (d.getClazz().equals(clazz)) {
                        // Give preference to properties that have the same class as that which was provided
                        columnName = d.getColumn().getName();
                        break;
                    } else if (columnName == null) {
                        // Otherwise, just use the first match that was found (presumably from a base class)
                        columnName = d.getColumn().getName();
                    }
                }
            }
        }
        return columnName;
    }

    @Override
    public String toString() {
        return modelClassMetaData.toString();
    }

}
