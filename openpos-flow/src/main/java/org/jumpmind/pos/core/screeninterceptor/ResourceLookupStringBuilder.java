package org.jumpmind.pos.core.screeninterceptor;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.util.DefaultObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Builds resource strings with named parameters, indexed parameters or
 * a combination of the two. Strings can then be looked up by key or key+group
 * in Java resource bundles and have their parameters and/or arguments substituted
 * at runtime.  The {@code key} is the property name and the {@code group} is the bundle
 * name.  For example, a key value of {@code first.name} and group value of {@code pos}
 * would map to a lookup of the {@code first.name} property value in the {@code pos} bundle 
 * (e.g., {@code pos_en.properties} for US Enlish locale.)<br/><br/>
 * 
 * Example to lookup a resource named 'some.property' with no parameters or arguments:<br/>
 * <br/>
 * <pre>
 *      String jsonStr = new ResourceLookupStringBuilder("some.property").toJson();
 * </pre>
 * Results in jsonStr value of <code>{"key":"some.property"}</code>.
 * <br/><br/>
 * 
 * Example to lookup a resource named 'some.property' with a parameter named 'variable1':<br/>
 * <br/>
 * <pre>
 *      String jsonStr = new ResourceLookupStringBuilder("some.property").addParameter("variable1", "value1").toJson();
 * </pre>
 * Results in jsonStr value of <code>{"key":"some.property","parameters":{"variable1":"value1"}}</code>.
 * <br/><br/>
 * 
 * Example to lookup a resource named 'some.property' with a parameter named 'variable1' and 
 * an indexed argument:<br/>
 * <br/>
 * <pre>
 *      String jsonStr = new ResourceLookupStringBuilder("some.property", "pos").addParameter("variable1", "value1").addIndexedArg("abc").toJson();
 * </pre>
 * Results in jsonStr value of <code>{"key":"some.property","parameters":{"variable1":"value1"},"indexedArgs":["abc"]}</code>.
 * <br/><br/>
 * 
 */
public class ResourceLookupStringBuilder implements Serializable{

	Logger logger = LoggerFactory.getLogger(getClass());
	
	private static final long serialVersionUID = 1L;
	private String key;
	private String group;
	private Map<String, String> parameters;
	private List<String> indexedArgs;
	
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
	public void setIndexedArgs(String...args) {
	    this.indexedArgs = args != null ? Arrays.asList(args) : null;
	}
	
	public String[] getIndexedArgs() {
	    return this.indexedArgs != null ? this.indexedArgs.toArray(new String[] {}) : null;
	}
	
	public ResourceLookupStringBuilder addIndexedArg(String arg) {
	    return this.addIndexedArgs(arg);
	}

    public ResourceLookupStringBuilder addIndexedArgs(String... args) {
        if (this.indexedArgs == null) {
            this.indexedArgs = new ArrayList<>();
        }
        this.indexedArgs.addAll(Arrays.asList(args));
        
        return this;
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
