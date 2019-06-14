package org.jumpmind.pos.core.screen;

public class Line {
    
    String message;
    String cssClass;
    
    public Line() {
        
    }
    
    public Line(String message) {
        this.message = message;
    }
    
    public Line(String message, String cssClass) {
        this.message = message;
        this.cssClass = cssClass;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getCssClass() {
        return cssClass;
    }
    
    public void setCssClass(String cssClass) {
        this.cssClass = cssClass;
    }
}