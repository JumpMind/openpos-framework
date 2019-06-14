package org.jumpmind.pos.core.flow;

import java.lang.reflect.Field;

public interface IScopeValueProvider {
    
    public ScopeValue getValue(String name, ScopeType scope, Object target, Field field);

}
