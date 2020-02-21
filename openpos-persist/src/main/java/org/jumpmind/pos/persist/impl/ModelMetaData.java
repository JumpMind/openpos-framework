package org.jumpmind.pos.persist.impl;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

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

    @Override
    public String toString() {
        return modelClassMetaData.toString();
    }
}
