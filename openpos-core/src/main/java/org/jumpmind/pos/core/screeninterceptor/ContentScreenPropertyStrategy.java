package org.jumpmind.pos.core.screeninterceptor;

import java.util.Map;

import org.jumpmind.pos.core.content.ContentProviderService;
import org.jumpmind.pos.core.ui.UIMessage;
import org.springframework.beans.factory.annotation.Autowired;

public class ContentScreenPropertyStrategy implements IScreenPropertyStrategy {

    @Autowired
    ContentProviderService contentProviderService;

    @Override
    public Object doStrategy(String appId, String deviceId, Object property, Class<?> clazz, UIMessage screen,
            Map<String, Object> screenContext) {

        if (contentProviderService != null && String.class.equals(clazz)) {
            String value = (String) property;
            if (value != null && value.startsWith("content:")) {
                String[] parts = value.split(":");
                String key = null;
                if (parts.length == 2) {
                    key = parts[1];
                }

                if (key != null) {
                    return contentProviderService.resolveContent(deviceId, key);
                }
            }
        }

        return property;
    }

}
