package org.jumpmind.pos.core.flow;

import java.lang.reflect.Field;

public class OutjectedValue {
    
    private Field field;
    private Object value;
    
    public OutjectedValue(Field field, Object value) {
        super();
        this.field = field;
        this.value = value;
    }
    
    public Field getField() {
        return field;
    }
    public void setField(Field field) {
        this.field = field;
    }
    public Object getValue() {
        return value;
    }
    public void setValue(Object value) {
        this.value = value;
    }

}
