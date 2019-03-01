package org.jumpmind.pos.service;

import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.service.instrumentation.ServiceSample;
import org.jumpmind.pos.service.strategy.IInvocationStrategy;
import org.jumpmind.pos.util.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

@Component
public class EndpointDispatchInvocationHandler implements InvocationHandler {

    @Autowired
    Map<String, IInvocationStrategy> strategies;

    @Autowired
    private ServiceConfig serviceConfig;
    
    final static int MAX_SUMMARY_WIDTH = 127;
    
    @Autowired
    @Qualifier("ctxSession")
    @Lazy
    private DBSession dbSession;
    
    @Value("${openpos.installationId}")
    String installationId;
    
    static BasicThreadFactory factory = new BasicThreadFactory.Builder()
            .namingPattern("service-instrumentation-thread-%d")
            .daemon(true)
            .build();
    private static final ExecutorService instrumentationExecutor = Executors.newSingleThreadExecutor(factory);    
    
    public EndpointDispatchInvocationHandler() {
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().equals("equals")) {
            return false;
        }

        ServiceSpecificConfig config = getSpecificConfig(method);
        IInvocationStrategy strategy = strategies.get(config.getStrategy().name());
        ServiceSample sample = startSample(strategy, config, proxy, method, args);
        Object result = null;
        try {            
            result = strategy.invoke(config, proxy, method, args);
            endSampleSuccess(sample, config, proxy, method, args, result);
        } catch (Throwable ex) {
            endSampleError(sample, config, proxy, method, args, result, ex);
            throw ex;
        }
        
        return result;        
    }
    
    private ServiceSpecificConfig getSpecificConfig(Method method) {
        Class<?> methodClazz = method.getDeclaringClass();
        RestController restController = methodClazz.getAnnotation(RestController.class);
        if (restController != null && isNotBlank(restController.value())) {
            return serviceConfig.getServiceConfig(installationId, restController.value());
        } else {
            throw new IllegalStateException(methodClazz.getSimpleName() + " must declare @" + RestController.class.getSimpleName()
                    + " and it must have the value() attribute set");
        }
    }
    

    protected ServiceSample startSample(IInvocationStrategy strategy, ServiceSpecificConfig config, Object proxy, Method method, Object[] args) {
        ServiceSample sample = new ServiceSample();
        sample.setSampleId(installationId + System.currentTimeMillis());
        sample.setInstallationId(installationId);
        sample.setHostname(AppUtils.getHostName());
        sample.setServiceName(method.getDeclaringClass().getSimpleName() + "." + method.getName());
        sample.setServiceType(strategy.getStrategyName());
        sample.setStartTime(new Date());
        return sample;
    }
    
    protected void endSampleSuccess(ServiceSample sample, ServiceSpecificConfig config, Object proxy, Method method, Object[] args, Object result) {
        if (result != null) {            
            sample.setServiceResult(StringUtils.abbreviate(result.toString(), MAX_SUMMARY_WIDTH));
        }
        endSample(sample, config, proxy, method, args);
    }
    
    protected void endSampleError(ServiceSample sample, ServiceSpecificConfig config, Object proxy, Method method, Object[] args, Object result, Throwable ex) {
        sample.setServiceResult(null);
        sample.setErrorFlag(true);
        sample.setErrorSummary(StringUtils.abbreviate(ex.getMessage(), MAX_SUMMARY_WIDTH));
        endSample(sample, config, proxy, method, args);
    }
    
    protected void endSample(ServiceSample sample, ServiceSpecificConfig config, Object proxy, Method method, Object[] args) {
        sample.setEndTime(new Date());
        sample.setDurationMs(sample.getEndTime().getTime()-sample.getStartTime().getTime());
        instrumentationExecutor.execute(new Runnable() {
            @Override
            public void run() {
                dbSession.save(sample);        
            }
        });
    }
    
}
