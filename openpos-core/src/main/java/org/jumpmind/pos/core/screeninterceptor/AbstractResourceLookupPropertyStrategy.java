package org.jumpmind.pos.core.screeninterceptor;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jumpmind.pos.core.service.IResourceLookupService;
import org.jumpmind.pos.util.model.Message;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractResourceLookupPropertyStrategy<T extends Message> implements IMessagePropertyStrategy<T> {

    protected static final Pattern PARAM_PATTERN = Pattern.compile("(\\{\\{.*?\\}\\})");
    
	@Autowired
	IResourceLookupService lookupService;
	
	@Override
	public Object doStrategy(String appId, String deviceId, Object property, Class<?> clazz, T message, Map<String, Object> screenContext) {
		if( String.class.equals(clazz)) {
			String value = (String)property;
			if(value != null && value.startsWith("{")) {
				try {
					ResourceLookupStringBuilder lookupObject = ResourceLookupStringBuilder.fromJson(value);

					String group = lookupObject.getGroup() != null ? lookupObject.getGroup() : "common";
					String originalValue = lookupService.getString(appId, deviceId, group, lookupObject.getKey()); 
					String newValue = originalValue;
					if(lookupObject.getParameters() != null) {
						newValue = lookupObject.getParameters().keySet().stream().reduce( newValue, (acc, key) -> acc.replace("{{" + key + "}}", lookupObject.getParameters().get(key)));
					}
					
					// Also check for references to properties that are defined within
					// the resources used by the lookupService itself.
					Matcher m = PARAM_PATTERN.matcher(newValue);
					StringBuffer tempValue= new StringBuffer(newValue.length());
					boolean atLeastOneMatch = false;
					while (m.find()) {
					    atLeastOneMatch = true;
					    String match = m.group(1);
					    String param = match.substring(2,match.length()-2);
					    String replacement = this.lookupService.getString(appId, deviceId, group, param);
					    if (replacement != null) {
    					    // Make sure the replacement doesn't also have additional
    					    // property references in it.  We don't support that (yet).
    					    Matcher m2 = PARAM_PATTERN.matcher(replacement);
    					    if (m2.find()) {
    					        throw new RuntimeException(
    					            String.format("The parameter '%s' in property '%s' " + 
    					                "refers to at least one other property with " +
    					                "parameters in it. Only one level of property " +
    					                " references is supported.",
    					                param, originalValue )
    					        );
    					    }
					        m.appendReplacement(tempValue, Matcher.quoteReplacement(replacement));
					    }
					}
					
					if (atLeastOneMatch) {
					    m.appendTail(tempValue);
					    newValue = tempValue.toString();
					}
					
					return newValue;
				} catch( IllegalArgumentException | MissingResourceException e) {
					return property;
				}
			} else if (value != null && value.startsWith("key:")) {
				// TODO: Phase out this approach. leaving in for now for backwards compatibility
	            String[] parts = value.split(":");
	            String group = "common";
	            String key = null;
	            if (parts.length >= 3) {
	                group = parts[1];
	                key = parts[2];
	            } else if (parts.length == 2) {
	                key = parts[1];
	            }

	            if (key != null) {
	                return lookupService.getString(appId, deviceId,group, key);
	            }
	        }
		}
		
		return property;
       
	}

}
