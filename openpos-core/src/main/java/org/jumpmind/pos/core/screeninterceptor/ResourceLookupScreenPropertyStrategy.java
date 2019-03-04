package org.jumpmind.pos.core.screeninterceptor;

import java.util.MissingResourceException;

import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.service.IResourceLookupService;
import org.springframework.beans.factory.annotation.Autowired;

public class ResourceLookupScreenPropertyStrategy implements IScreenPropertyStrategy {

	@Autowired
	IResourceLookupService lookupService;
	
	@Override
	public Object doStrategy(String appId, String deviceId, Object property, Class<?> clazz, Screen screen) {
		if( String.class.equals(clazz)) {
			String value = (String)property;
			if(value != null && value.startsWith("{")) {
				try {
					ResourceLookupStringBuilder lookupObject = ResourceLookupStringBuilder.fromJson(value);

					String group = lookupObject.getGroup() != null ? lookupObject.getGroup() : "common";
					String newValue = lookupService.getString(appId, deviceId, group, lookupObject.getKey());
					if(lookupObject.getParameters() != null) {
						newValue = lookupObject.getParameters().keySet().stream().reduce( newValue, (acc, key) -> acc.replace("{{" + key + "}}", lookupObject.getParameters().get(key)));
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
