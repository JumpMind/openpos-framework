package org.jumpmind.pos.core.service;

import java.util.Map;

import org.jumpmind.pos.core.ui.UIDataMessage;
import org.jumpmind.pos.core.ui.UIMessage;

public interface IKeyMappingService {
	
    String getKeyMapping(UIMessage screen, String actionName, Map<String, Object> screenContext);
    String getKeyMapping(UIDataMessage message, String actionName);

}
