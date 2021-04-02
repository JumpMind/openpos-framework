package org.jumpmind.pos.persist.impl;

import org.jumpmind.pos.persist.PersistException;

public class DmlTemplate implements Cloneable {

    String name;
    String dml;
    
    public void setDml(String dml) {
        this.dml = dml;
    }
    
    public String getDml() {
        return dml;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }

    public DmlTemplate copy() {
        try {
            return (DmlTemplate)this.clone();
        } catch (CloneNotSupportedException e) {
            throw new PersistException(e);
        }
    }

}
