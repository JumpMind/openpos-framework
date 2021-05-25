package org.jumpmind.pos.core.screeninterceptor;

import java.util.Map;

import org.jumpmind.pos.util.model.Message;

public interface IMessagePropertyStrategy<T extends Message> {
	Object doStrategy(String deviceId, Object property, Class<?> clazz, T message, Map<String, Object> messageContext);
}
