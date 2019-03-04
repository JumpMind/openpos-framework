package org.jumpmind.pos.core.screeninterceptor;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.service.IKeyMappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

public class KeyMappingScreenPropertyStrategy implements IScreenPropertyStrategy {

	@Autowired
	IKeyMappingService keyMappingService;
	
	@Override
	public Object doStrategy(String appId, String deviceId, Object property, Class<?> clazz, Screen screen) {
		if(property != null 
		        && ActionItem.class.isAssignableFrom(clazz)) {
			ActionItem item = (ActionItem)property;
			String keyMapping = keyMappingService.getKeyMapping(screen.getId(), item.getAction());
			if (!StringUtils.isEmpty(keyMapping)) {			    
			    item.setKeybind(keyMapping);
			}
			return item;
		}
		return property;
	}

}
