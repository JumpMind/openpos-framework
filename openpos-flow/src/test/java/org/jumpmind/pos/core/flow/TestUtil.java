package org.jumpmind.pos.core.flow;

import java.lang.reflect.Field;

public class TestUtil {

    public static void setField(Object target, String fieldName, Object value) throws Exception {
        Field actionHandlerField = target.getClass().getDeclaredField(fieldName);
        actionHandlerField.setAccessible(true);
        actionHandlerField.set(target, value);        
    }
    
}
