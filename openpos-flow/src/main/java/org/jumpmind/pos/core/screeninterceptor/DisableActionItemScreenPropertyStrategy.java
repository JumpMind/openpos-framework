package org.jumpmind.pos.core.screeninterceptor;

import java.util.Map;

import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.service.IDisableActionItemService;
import org.jumpmind.pos.core.ui.UIMessage;
import org.springframework.beans.factory.annotation.Autowired;

public class DisableActionItemScreenPropertyStrategy implements IMessagePropertyStrategy<UIMessage> {

	@Autowired( required = false )
	IDisableActionItemService disableActionItemService;
	
	@Override
	public Object doStrategy(String deviceId, Object property, Class<?> clazz, UIMessage screen, Map<String, Object> screenContext) {
		if(property != null && ActionItem.class.equals(clazz)) {
			ActionItem item = (ActionItem)property;
			// Only try to disable if it is currently disabled. This way we don't enable buttons that were
			// explicitly disabled.
			if( item.isEnabled() ) {
				item.setEnabled(!disableActionItemService.isActionDisabled(deviceId, item.getAction()));
			}
			return item;
		}
		
		return property;
	}
}
