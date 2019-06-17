package org.jumpmind.pos.core.screeninterceptor;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jumpmind.pos.util.DefaultObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

public class ResourceLookupStringBuilder implements Serializable{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private String key;
	private String group;
	private Map<String, String> parameters;
	
	public ResourceLookupStringBuilder() {
	}
	
	public ResourceLookupStringBuilder( String key ){
		setKey(key);
	}
	
	public ResourceLookupStringBuilder( String key, String group){
		setKey(key);
		setGroup(group);
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	public ResourceLookupStringBuilder addParameter(String name, String value) {
		if( parameters == null) {
			parameters = new HashMap<String, String>();
		}
		parameters.put(name, value);
		
		return this;
	}
	
    public static String toJson(String key) {
        return new ResourceLookupStringBuilder(key).toJson();
    }
    
    public static String toJson(String key, String group) {
        return new ResourceLookupStringBuilder(key, group).toJson();
    }
    
	public String toJson() {
		try {
			return DefaultObjectMapper.build().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error("Failed to convert resource string to json", e);
		}
		
		return null;
	}
	
	public static ResourceLookupStringBuilder fromJson( String value ) {
		try {
			return DefaultObjectMapper.build().readValue(value, ResourceLookupStringBuilder.class);
		} catch (IOException e) {
			LoggerFactory.getLogger(ResourceLookupStringBuilder.class).error("Failed to convert json to resource object");
		}
		return null;
	}
}
