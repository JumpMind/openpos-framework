package org.jumpmind.pos.core.screeninterceptor;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.core.flow.IStateManager;
import org.jumpmind.pos.core.flow.In;
import org.jumpmind.pos.core.flow.ScopeType;
import org.jumpmind.pos.core.service.IKeyMappingService;
import org.jumpmind.pos.core.ui.ActionItem;
import org.jumpmind.pos.core.ui.UIDataMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
// ProxyMode so this becomes a DeviceScope bean when being @Autowired into a
// list of interfaces
@Scope(proxyMode = org.springframework.context.annotation.ScopedProxyMode.TARGET_CLASS, value = "device")
public class KeyMappingUIDataMessagePropertyStrategy implements IMessagePropertyStrategy<UIDataMessage> {

    @Autowired(required = false)
    IKeyMappingService keyMappingService;

    @In(scope = ScopeType.Device)
    private IStateManager stateManager;

    @Override
    public Object doStrategy(
            String deviceId,
            Object property,
            Class<?> clazz,
            UIDataMessage message,
            Map<String, Object> screenContext) {
        if (property != null && ActionItem.class.isAssignableFrom(clazz) && keyMappingService != null) {
            ActionItem item = (ActionItem) property;
            if (item.isAutoAssignEnabled()) {
                String keyMapping = keyMappingService.getKeyMapping(message, item.getAction());
                if (!StringUtils.isEmpty(keyMapping)) {
                    item.setKeybind(keyMapping);
                    String keyMappingDisplayName = keyMappingService.getDisplayName(message.getDataType(), keyMapping);
                    item.setKeybindDisplayName(keyMappingDisplayName);
                }
            }
        }
        return property;
    }

}
