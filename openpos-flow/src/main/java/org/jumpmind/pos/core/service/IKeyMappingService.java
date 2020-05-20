package org.jumpmind.pos.core.service;

import org.jumpmind.pos.core.ui.UIDataMessage;
import org.jumpmind.pos.core.ui.UIMessage;

import java.util.Map;

public interface IKeyMappingService {
	
    String getKeyMapping(UIMessage screen, String actionName, Map<String, Object> screenContext);
    String getKeyMapping(UIDataMessage message, String actionName);
    String getDisplayName(String groupName, String keyMapping);
}
