package org.jumpmind.pos.core.screeninterceptor;

import java.util.Map;

import org.jumpmind.pos.core.ui.UIMessage;

/**
 * Use IMessagePropertyStrategy instead
 * @deprecated
 */
@Deprecated
public interface IScreenPropertyStrategy {
	Object doStrategy(String appId, String deviceId, Object property, Class<?> clazz, UIMessage screen, Map<String, Object> screenContext);
}
