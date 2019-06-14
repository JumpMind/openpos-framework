package org.jumpmind.pos.core.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY)
public interface IFormElement {
    public String getId();
    public void setId(String id);
}
