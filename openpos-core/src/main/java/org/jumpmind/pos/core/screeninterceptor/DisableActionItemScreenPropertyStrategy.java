package org.jumpmind.pos.core.screeninterceptor;

import org.jumpmind.pos.core.screen.ActionItem;
import org.jumpmind.pos.core.screen.Screen;
import org.jumpmind.pos.core.service.IDisableActionItemService;
import org.springframework.beans.factory.annotation.Autowired;

public class DisableActionItemScreenPropertyStrategy implements IScreenPropertyStrategy {

	@Autowired( required = false )
	IDisableActionItemService disableActionItemService;
	
	@Override
	public Object doStrategy(String appId, String deviceId, Object property, Class<?> clazz, Screen screen) {
		if(property != null && ActionItem.class.equals(clazz)) {
			ActionItem item = (ActionItem)property;
			item.setEnabled(!disableActionItemService.isActionDisabled(appId, deviceId, item.getAction()));
			return item;
		}
		
		return property;
	}
}
