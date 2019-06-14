package org.jumpmind.pos.core.model;

public interface IField {
    public String getLabel();
    public void setLabel(String label);
    
    public String getId();
    public void setId(String id);
    
    public String getValue();
    public void setValue(String value);
}
