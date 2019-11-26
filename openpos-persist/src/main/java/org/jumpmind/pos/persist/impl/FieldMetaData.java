package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;

public class FieldMetaData {
    private Class<?> clazz;
    private Field field;

    public FieldMetaData(Class<?> clazz, Field field) {
        this.clazz = clazz;
        this.field = field;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
