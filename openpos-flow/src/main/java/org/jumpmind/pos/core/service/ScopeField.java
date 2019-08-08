package org.jumpmind.pos.core.service;

import java.io.Serializable;

public class ScopeField implements Serializable {

    private static final long serialVersionUID = 1L;
    private String name;
    private String date;
    private String stackTrace;
    
    public ScopeField(String name, String date, String stackTrace) {
    	this.name = name;
    	this.date = date;
    	this.stackTrace = stackTrace;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public String getDate() {
    	return this.date;
    }
    
    public String getStackTrace() {
    	return this.stackTrace;
    }
}
