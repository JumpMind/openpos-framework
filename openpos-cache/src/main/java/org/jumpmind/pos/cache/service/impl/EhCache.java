package org.jumpmind.pos.cache.service.impl;

public class EhCache implements ICache {
    
    private String name;
    
    public EhCache(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public Object getValue(String key) {
        return null;
    }

    @Override
    public void setValue(String key, Object value) {
    }

    @Override
    public void clearKey(String key) {
    }

    @Override
    public void clearAll() {
    }

}
