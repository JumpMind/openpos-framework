package org.jumpmind.pos.core.util;

import java.io.Serializable;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.remoting.rmi.RmiBasedExporter;
import org.springframework.remoting.rmi.RmiInvocationHandler;
import org.springframework.remoting.support.DefaultRemoteInvocationFactory;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationFactory;
import org.springframework.stereotype.Component;

/**
 * Handles creation of and caching of Spring-based RMI proxy objects on whom
 * remote method invocations can be made from the server side back to the client.
 * Provides ability to dynamically wrap an object to make it Remote-able so that
 * the object doesn't have to extend/implement java.rmi.Remote.
 *
 */
@Component
public class RMICallbackProxyManager {
    private final static Logger logger = LoggerFactory.getLogger(RMICallbackProxyManager.class);

    protected Map<Object, Remote> proxyMap = new HashMap<Object, Remote>();

    @SuppressWarnings("unchecked")
    public <T> T registerAndExport(Object target, Class<T> clazz) {

        // Dynamically create a Remote for the given target object. Using
        // this approach, the target object does not have to implement the
        // java.rmi.Remote interface
        AnonymousRmiServiceExporter a = new AnonymousRmiServiceExporter();
        a.setService(target);
        a.setServiceInterface(clazz);
        Remote remoteableTarget = a.getObjectToExport();  // returns an instance of a Spring RmiInvocationWrapper

        // Cache the remoteable object in case we need it later
        proxyMap.put(target, remoteableTarget);
        
        // Create a Remote stub to the proxy 
        RmiInvocationHandler stub = null;
        try {
            // Passing in port of 0, which will cause RMI framework to choose the port
            stub = (RmiInvocationHandler) UnicastRemoteObject.exportObject(remoteableTarget, 0);
            logger.debug("Stub {} for target object {} successfully created.", stub, target);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

        // Wrap the stub in an AOP proxy so that remote
        // method invocations make it back to the original
        // target object.
        MethodInterceptor mi = new TargetInterfaceWrapper(stub);
        ProxyFactory pf = new ProxyFactory(clazz, mi);
        pf.addInterface(Serializable.class);
        return (T) pf.getProxy();
    }

    public void unregister(Object target) {
        Remote remoteProxy = proxyMap.remove(target);
        try {
            UnicastRemoteObject.unexportObject(remoteProxy, true);
        } catch (NoSuchObjectException e) {
            logger.debug(String.format("Target object %s not exported successfully from RMI runtime", target ),e );
        }
    }

    /**
     * Subclass of existing Spring RmiBasedExporter to expose access to the proxy
     * which handles the remote method invocations back to the client.
     *
     */
    public static class AnonymousRmiServiceExporter extends RmiBasedExporter {
        public Remote getObjectToExport() {
            return super.getObjectToExport();
        }
    }

    public static class TargetInterfaceWrapper implements MethodInterceptor, Serializable {

        private static final long serialVersionUID = 1L;

        // This must be static, not sure why
        private static RemoteInvocationFactory remoteInvocationFactory = new DefaultRemoteInvocationFactory();

        private RmiInvocationHandler stub = null;

        public TargetInterfaceWrapper(RmiInvocationHandler stub) {
            this.stub = stub;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        public Object invoke(MethodInvocation invocation) throws Throwable {
            RemoteInvocation remoteInvocation = remoteInvocationFactory.createRemoteInvocation(invocation);
            try {
                return stub.invoke(remoteInvocation);
            } catch (Exception e) {
                Class<?>[] declaredExceptions = invocation.getMethod().getExceptionTypes();
                for (Class exceptionClass : declaredExceptions) {
                    if (exceptionClass.isAssignableFrom(e.getClass())) {
                        throw e;
                    }
                }
                throw new RemoteAccessException(e.getMessage(), e);
            }
        }
    }
}
