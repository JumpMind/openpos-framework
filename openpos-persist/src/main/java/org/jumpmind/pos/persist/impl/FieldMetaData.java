package org.jumpmind.pos.persist.impl;

import java.lang.reflect.Field;
import org.jumpmind.db.model.Column;

public class FieldMetaData {
    private Class<?> clazz;
    private Field field;
    private Column column;

    public FieldMetaData(Class<?> clazz, Field field, Column column) {
        this.clazz = clazz;
        this.field = field;
        this.column = column;
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

    public Column getColumn() {
        return column;
    }

    public void setColumn(Column column) {
        this.column = column;
    }
}
