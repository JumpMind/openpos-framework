package org.jumpmind.pos.cache.service.impl;


public interface ICache {
    
    public String getName();
    public Object getValue(String key);
    public void setValue(String key, Object value);
    public void clearKey(String key);
    public void clearAll();
    
}
