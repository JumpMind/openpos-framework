package org.jumpmind.pos.service.strategy;

import org.jumpmind.pos.service.NeedsActionException;
import org.jumpmind.pos.service.ServiceSpecificConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.lang.reflect.Method;
import java.util.Map;

@Component(RemoteFirstStrategy.REMOTE_FIRST_STRATEGY)
public class RemoteFirstStrategy implements IInvocationStrategy {

    final Logger logger = LoggerFactory.getLogger(getClass());
    
    static final String REMOTE_FIRST_STRATEGY = "REMOTE_FIRST";

    @Autowired
    @Qualifier("LOCAL_ONLY")
    LocalOnlyStrategy localStrategy;

    @Autowired
    RemoteOnlyStrategy remoteStrategy;

    @Override
    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {
        try {
            return remoteStrategy.invoke(config, proxy, method, endpoints, args);
        } catch (ResourceAccessException ex) {
            try {
                logger.info("Remote service unavailable.  Trying local");
                return localStrategy.invoke(config, proxy, method, endpoints, args);
            } catch (NeedsActionException nae) {
                throw nae;
            } catch (Exception e) {
                logger.info("Local call failed (this was the error).  Throwing original exception", e);
                throw ex;
            }
        }
    }

    @Override
    public String getStrategyName() {
        return REMOTE_FIRST_STRATEGY;
    }

}
