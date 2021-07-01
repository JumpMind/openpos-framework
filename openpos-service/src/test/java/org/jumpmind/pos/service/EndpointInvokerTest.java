package org.jumpmind.pos.service;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.service.instrumentation.Sample;
import org.jumpmind.pos.service.instrumentation.ServiceSampleModel;
import org.jumpmind.pos.service.strategy.IInvocationStrategy;
import org.jumpmind.pos.service.strategy.LocalOnlyStrategy;
import org.jumpmind.pos.util.SuppressMethodLogging;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class EndpointInvokerTest {
    @SuppressMethodLogging
    public void TestMethodNotAnnotated() {}

    @Sample
    public void TestMethodAnnotated() {}

    String installationId = "TestInstallationId";

    private ServiceSpecificConfig getServiceSpecificConfig() {
        ServiceSpecificConfig config = new ServiceSpecificConfig();
        SamplingConfig sampleConfig = new SamplingConfig();
        sampleConfig.setEnabled(true);
        config.setSamplingConfig(sampleConfig);

        EndpointSpecificConfig endpointSpecificConfig = new EndpointSpecificConfig();
        endpointSpecificConfig.setPath("/test/one");
        endpointSpecificConfig.setSamplingConfig(sampleConfig);

        List<EndpointSpecificConfig> endpoints = new ArrayList<>();
        endpoints.add(endpointSpecificConfig);

        config.setEndpoints( endpoints);
        return config;
    }

    @Test
    public void invokeStrategySamplesAndCallsInvokeTest() throws Throwable {
        IInvocationStrategy invocationStrategy = mock(LocalOnlyStrategy.class);
        List<String> profileIds = new ArrayList<>();
        Method method = EndpointInvokerTest.class.getMethod("TestMethodNotAnnotated");

        ServiceSpecificConfig config = getServiceSpecificConfig();
        String path = "/test/one";

        DBSession session = mock(DBSession.class);
        EndpointInvoker endpointInvoker = spy(new EndpointInvoker());
        endpointInvoker.dbSession = session;

        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").strategy(invocationStrategy).endpoint(null).endpointPath(path).arguments(null).
                method(method).build();

        doReturn(new Object()).when(invocationStrategy).invoke(eq(context));

        Object result = endpointInvoker.invokeStrategy(context);

        verify(endpointInvoker, atLeastOnce()).startSample(context);
        verify(invocationStrategy, atLeastOnce()).invoke(context);
        verify(endpointInvoker, atLeastOnce()).endSampleSuccess(anyObject(), eq(context));
        assertNotNull(result, "invokeStrategy should not return null in this case.");
    }

    @Test
    public void invokeStrategyEndsSampleAndThrowsTest() throws Throwable {
        IInvocationStrategy invocationStrategy = mock(LocalOnlyStrategy.class);
        List<String> profileIds = new ArrayList<>();
        Method method = EndpointInvokerTest.class.getMethod("TestMethodNotAnnotated");

        Exception exception = new Exception();
        doThrow(exception).when(invocationStrategy).invoke(eq(profileIds), eq(null), eq(method), anyObject(), eq(null));


        ServiceSpecificConfig config = getServiceSpecificConfig();

        String path = "/test/one";

        DBSession session = mock(DBSession.class);
        EndpointInvoker endpointInvoker = spy(new EndpointInvoker());
        endpointInvoker.dbSession = session;

        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").strategy(invocationStrategy).endpoint(null).endpointPath(path).arguments(null).
                method(method).build();

        try{
            Object result = endpointInvoker.invokeStrategy(context);
        } catch (Throwable ex) {
            verify(endpointInvoker, atLeastOnce()).startSample(context);
            verify(invocationStrategy, atLeastOnce()).invoke(eq(context));
            verify(endpointInvoker, never()).endSampleSuccess(anyObject(), eq(context));
            verify(endpointInvoker, atLeastOnce()).endSampleError(anyObject(), eq(context), eq(ex));
            if (!ex.equals(exception)) {
                throw ex;
            }
        }
    }

    private ServiceSampleModel getServiceSampleModel() {
        ServiceSampleModel sampleModel = mock(ServiceSampleModel.class, RETURNS_DEEP_STUBS);
        sampleModel.setStartTime(new Date());
        sampleModel.setEndTime(new Date());
        return sampleModel;
    }

    @Test
    public void startSampleReturnsNullWhenNotConfiguredAtModuleLevelToSampleMethodTest() throws NoSuchMethodException {
        IInvocationStrategy invocationStrategy = new LocalOnlyStrategy();

        Method method = EndpointInvokerTest.class.getMethod("TestMethodNotAnnotated");

        ServiceSpecificConfig config = getServiceSpecificConfig();
        config.getSamplingConfig().setEnabled(false);

        String path = "/test/one";

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").strategy(invocationStrategy).endpoint(null).endpointPath(path).arguments(null).
                method(method).build();
        ServiceSampleModel result = endpointInvoker.startSample(context);
        assertNull(result, "EndpointInvoker.startSample should return null when the method passed is not configured to be sampled");
    }

    @Test
    public void startSampleReturnsNullWhenNotConfiguredAtEndpointLevelToSampleMethodTest() throws NoSuchMethodException {
        IInvocationStrategy invocationStrategy = new LocalOnlyStrategy();

        Method method = EndpointInvokerTest.class.getMethod("TestMethodNotAnnotated");

        ServiceSpecificConfig config = getServiceSpecificConfig();
        config.getEndpoints().get(0).getSamplingConfig().setEnabled(false);

        String path = "/test/one";

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").strategy(invocationStrategy).endpoint(null).endpointPath(path).arguments(null).
                method(method).build();
        ServiceSampleModel result = endpointInvoker.startSample(context);
        assertNull(result, "EndpointInvoker.startSample should return null when the method passed is not configured to be sampled");
    }

    @Test
    public void startSampleReturnsNotNullWhenIsConfiguredToSampleMethodTest() throws NoSuchMethodException {
        IInvocationStrategy invocationStrategy = new LocalOnlyStrategy();

        Method method = EndpointInvokerTest.class.getMethod("TestMethodAnnotated");

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        endpointInvoker.installationId = installationId;

        ServiceSpecificConfig config = getServiceSpecificConfig();

        String path = "/test/one";
        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").strategy(invocationStrategy).endpoint(null).endpointPath(path).arguments(null).
                config(config).
                method(method).build();

        ServiceSampleModel result = endpointInvoker.startSample(context);

        assertNotNull(result, "EndpointInvoker.startSample should return not null when the method passed is configured to be sampled.");
    }

    @Test
    public void isSamplingEnabledAddsUnseenPathsToCache(){
        String path = "/test/one";
        ServiceSpecificConfig serviceSpecificConfig = serviceSpecificConfigSetup(path);

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        HashMap<String, Boolean> endpointCache = spy(new HashMap<>());
        endpointInvoker.endpointEnabledCache = endpointCache;

        boolean result = endpointInvoker.isSamplingEnabled(path, serviceSpecificConfig);

        verify(endpointCache, atLeastOnce()).get(path);
        verify(endpointCache, atLeastOnce()).put(path, true);
        assertEquals(endpointCache.size(), 1);
        assertEquals(result, true);
    }

    @Test
    public void isSamplingEnabledDoseNotAddSeenPathsToCache(){
        String path = "/test/one";
        ServiceSpecificConfig serviceSpecificConfig = serviceSpecificConfigSetup(path);

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        HashMap<String, Boolean> endpointCache = spy(new HashMap<>());
        endpointInvoker.endpointEnabledCache = endpointCache;

        endpointInvoker.isSamplingEnabled(path, serviceSpecificConfig);
        boolean result = endpointInvoker.isSamplingEnabled(path, serviceSpecificConfig);

        verify(endpointCache, atLeastOnce()).get(path);
        verify(endpointCache, atLeastOnce()).put(path, true);
        assertEquals(endpointCache.size(), 1);
        assertEquals(result, true);
    }

    @Test
    public void isSamplingEnabledDoseNotAddDisabledEndpointsAtEndpointLevel(){
        String path = "/test/one";
        ServiceSpecificConfig serviceSpecificConfig = serviceSpecificConfigSetup(path);

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        HashMap<String, Boolean> endpointCache = spy(new HashMap<>());
        endpointInvoker.endpointEnabledCache = endpointCache;

        serviceSpecificConfig.getEndpoints().get(0).getSamplingConfig().setEnabled(false);
        boolean result = endpointInvoker.isSamplingEnabled(path, serviceSpecificConfig);

        serviceSpecificConfig.getEndpoints().get(0).getSamplingConfig().setEnabled(true);
        serviceSpecificConfig.getSamplingConfig().setEnabled(false);
        endpointInvoker.isSamplingEnabled(path, serviceSpecificConfig);

        verify(endpointCache, atLeastOnce()).get(path);
        verify(endpointCache, atLeastOnce()).put(path, false);
        assertEquals(endpointCache.size(), 1);
        assertEquals(result, false);
    }

    @Test
    public void isSamplingEnabledDoseNotAddDisabledEndpointsAtModuleLevel(){
        String path = "/test/one";
        ServiceSpecificConfig serviceSpecificConfig = serviceSpecificConfigSetup(path);

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        HashMap<String, Boolean> endpointCache = spy(new HashMap<>());
        endpointInvoker.endpointEnabledCache = endpointCache;

        serviceSpecificConfig.getSamplingConfig().setEnabled(false);
        boolean result = endpointInvoker.isSamplingEnabled(path, serviceSpecificConfig);

        verify(endpointCache, atLeastOnce()).get(path);
        verify(endpointCache, atLeastOnce()).put(path, false);
        assertEquals(endpointCache.size(), 1);
        assertEquals(result, false);
    }

    @Test
    public void isSamplingEnabledReturnsNullWithConfigNull(){
        String path = "/test/one";

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        HashMap<String, Boolean> endpointCache = spy(new HashMap<>());
        endpointInvoker.endpointEnabledCache = endpointCache;

        boolean result = endpointInvoker.isSamplingEnabled(path, null);

        verify(endpointCache, atLeastOnce()).get(path);
        verify(endpointCache, atLeastOnce()).put(path, false);
        assertEquals(endpointCache.size(), 1);
        assertEquals(result, false);
    }

    private ServiceSpecificConfig serviceSpecificConfigSetup(String path) {
        ServiceSpecificConfig serviceSpecificConfig = new ServiceSpecificConfig();
        SamplingConfig samplingConfig = new SamplingConfig();
        samplingConfig.setEnabled(true);
        serviceSpecificConfig.setSamplingConfig(samplingConfig);

        List<EndpointSpecificConfig> endpoints = new ArrayList<>();
        EndpointSpecificConfig endpointSpecificConfig = new EndpointSpecificConfig();
        endpointSpecificConfig.setPath(path);
        endpointSpecificConfig.setSamplingConfig(samplingConfig);
        endpoints.add(endpointSpecificConfig);

        serviceSpecificConfig.setEndpoints(endpoints);

        return serviceSpecificConfig;
    }

    @Test
    public void startSampleReturnsAcceptableFieldsTest() throws NoSuchMethodException {
        IInvocationStrategy invocationStrategy = new LocalOnlyStrategy();

        Method method = EndpointInvokerTest.class.getMethod("TestMethodAnnotated");
        String simpleName = method.getDeclaringClass().getSimpleName();
        String methodName = method.getName();

        EndpointInvoker endpointInvoker = new EndpointInvoker();
        endpointInvoker.installationId = installationId;

        ServiceSpecificConfig config = getServiceSpecificConfig();

        String path = "/test/one";

        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").strategy(invocationStrategy).endpoint(null).endpointPath(path).arguments(null).
                config(config).
                method(method).build();

        ServiceSampleModel result = endpointInvoker.startSample(context);

        assertTrue(Pattern.matches(installationId + "\\d*", result.getSampleId()), "sampleId should have installationId followed by the system time in milliseconds.");
        assertEquals(installationId, result.getInstallationId(), "installationId should be populated with the installationId.");
        assertNotNull(result.getHostname(), "hostname should populate results with the systems hostname.");
        assertTrue(Pattern.matches(simpleName + "." + methodName, result.getServiceName()), "serviceName should be populated with the methodDeclaringClassSimpleName.methodName.");
        assertEquals(invocationStrategy.getStrategyName(), result.getServiceType(), "serviceType should be populated with the strategy name of the strategy passed.");
        assertNotNull(result.getStartTime(), "startTime should be populated with the data the approximate date the method was called.");
        assertNull(result.getServicePath(), "servicePath should not be set by startSample.");
        assertNull(result.getServiceResult(), "serviceResult should not be set by startSample.");
        assertNull(result.getEndTime(), "serviceResult should not be set by startSample.");
        assertEquals( 0, result.getDurationMs(), "durationMS should not be set by startSample.");
        assertFalse(result.isErrorFlag(), "errorFlag should not be set by startSample.");
        assertNull(result.getErrorSummary(), "errorSummary should not be set by startSample.");
    }

    @Test
    public void endSampleSuccessEndsSampleTest() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        ServiceSampleModel sampleModel = getServiceSampleModel();

        Object testResult = new Object();
        DBSession session = mock(DBSession.class);

        EndpointInvoker endpointInvoker = spy(new EndpointInvoker());
        endpointInvoker.dbSession = session;

        EndpointInvocationContext context = EndpointInvocationContext.builder().result(testResult).build();

        endpointInvoker.endSampleSuccess(sampleModel, context);

        verify(sampleModel, atLeastOnce()).setServiceResult(anyString());
        verify(endpointInvoker, atLeastOnce()).endSample(sampleModel, context); // this was all nulls
    }

    @Test
    public void endSampleErrorProperlySetsFieldsTest() throws NoSuchMethodException {
        ServiceSampleModel sampleModel = getServiceSampleModel();

        Object testResult = new Object();
        DBSession session = mock(DBSession.class);

        EndpointInvoker endpointInvoker = spy(new EndpointInvoker());
        endpointInvoker.dbSession = session;

        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").result(testResult).build();

        endpointInvoker.endSampleError(sampleModel, context, new Exception("Test"));

        verify(sampleModel, atLeastOnce()).setServiceResult(null);
        verify(sampleModel, atLeastOnce()).setErrorFlag(true);
        verify(sampleModel, atLeastOnce()).setErrorSummary(anyString());
        verify(endpointInvoker, atLeastOnce()).endSample(sampleModel, context);
    }

    @Test
    public void endSampleSetsDataAndSavesToDBSession() throws NoSuchMethodException, NoSuchFieldException, IllegalAccessException {
        ServiceSampleModel sampleModel = getServiceSampleModel();

        EndpointInvoker endpointInvoker = new EndpointInvoker();

        Field executor = EndpointInvoker.class.getDeclaredField("instrumentationExecutor");
        executor.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(executor, executor.getModifiers() & ~Modifier.FINAL);

        ExecutorService executorService = mock(ExecutorService.class);
        executor.set(endpointInvoker, executorService);

        EndpointInvocationContext context = EndpointInvocationContext.builder().
                clientVersion("version?").build();

        endpointInvoker.endSample(sampleModel, context);

        verify(sampleModel, atLeastOnce()).setEndTime(anyObject());
        verify(sampleModel, atLeastOnce()).setDurationMs(anyLong());
        verify(executorService, atLeastOnce()).execute(anyObject());
    }
}