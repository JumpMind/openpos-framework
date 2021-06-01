package org.jumpmind.pos.core.screeninterceptor;

import java.util.Map;

import org.jumpmind.pos.core.content.ContentProviderService;
import org.jumpmind.pos.core.ui.UIMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(110)
@Scope("device")
public class ContentScreenPropertyStrategy implements IMessagePropertyStrategy<UIMessage> {

    @Autowired
    ContentProviderService contentProviderService;

    @Override
    public Object doStrategy(String deviceId, Object property, Class<?> clazz, UIMessage screen,
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
