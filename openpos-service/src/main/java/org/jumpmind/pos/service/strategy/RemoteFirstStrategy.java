package org.jumpmind.pos.service.strategy;

import java.lang.reflect.Method;

import org.jumpmind.pos.service.ServiceSpecificConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("REMOTE_FIRST")
public class RemoteFirstStrategy implements IInvocationStrategy {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    LocalOnlyStrategy localStrategy;

    @Autowired
    RemoteOnlyStrategy remoteStrategy;

    @Override
    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Object[] args) throws Throwable {
        try {
            return remoteStrategy.invoke(config, proxy, method, args);
        } catch (Exception ex) {
            logger.warn("Remote call failed.  Trying local", ex);
            return localStrategy.invoke(config, proxy, method, args);
        }
    }

}
