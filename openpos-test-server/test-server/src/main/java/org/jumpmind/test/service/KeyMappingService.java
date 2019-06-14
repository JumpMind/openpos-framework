package org.jumpmind.test.service;

import org.jumpmind.pos.core.service.IKeyMappingService;
import org.jumpmind.pos.core.ui.UIMessage;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KeyMappingService implements IKeyMappingService {
    @Override
    public String getKeyMapping(UIMessage screen, String actionName, Map<String, Object> screenContext) {
        return actionName;
    }
}
