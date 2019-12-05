package org.jumpmind.pos.core.model;

import java.io.Serializable;

public class HelpText implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String text;
    private String title;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
}
