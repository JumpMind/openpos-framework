package org.jumpmind.pos.service.strategy;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.jumpmind.pos.service.ServiceSpecificConfig;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Map;

@Component(SimulatedRemoteStrategy.SIMULATED_REMOTE_STRATEGY)
public class SimulatedRemoteStrategy extends LocalOnlyStrategy implements IInvocationStrategy {

    static final String SIMULATED_REMOTE_STRATEGY = "SIMULATED_REMOTE";

    @Override
    public String getStrategyName() {
        return SIMULATED_REMOTE_STRATEGY;
    }

    @Override
    public Object invoke(ServiceSpecificConfig config, Object proxy, Method method, Map<String, Object> endpoints, Object[] args) throws Throwable {
        ObjectMapper mapper = DefaultObjectMapper.build();
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        Object[] newArgs = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            newArgs[i] = mapper.readValue(mapper.writeValueAsString(args[i]), args[i].getClass());
        }
        Object retObj = super.invoke(config, proxy, method, endpoints, newArgs);
        return mapper.readValue(mapper.writeValueAsString(retObj), retObj.getClass());
    }
}
