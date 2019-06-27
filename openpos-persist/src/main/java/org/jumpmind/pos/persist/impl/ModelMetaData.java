package org.jumpmind.pos.persist.impl;

import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.List;

public class ModelMetaData {

    private List<ModelClassMetaData> modelClassMetaData;
    private Field systemInfoField;


    public List<ModelClassMetaData> getModelClassMetaData() {
        return modelClassMetaData;
    }

    public void setModelClassMetaData(List<ModelClassMetaData> modelClassMetaData) {
        this.modelClassMetaData = modelClassMetaData;
    }

    public Field getSystemInfoField() {
        return systemInfoField;
    }

    public void setSystemInfoField(Field systemInfoField) {
        this.systemInfoField = systemInfoField;
    }

    public void init() {
        systemInfoField = FieldUtils.getField(modelClassMetaData.get(modelClassMetaData.size()-1).getClass(), "systemInfo", true);
    }
}
