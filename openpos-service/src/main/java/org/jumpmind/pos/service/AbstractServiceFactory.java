package org.jumpmind.pos.service;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.annotation.Autowired;

abstract public class AbstractServiceFactory {

    @Autowired
    protected EndpointInvoker dispatcher;

    @SuppressWarnings("unchecked")
    protected <T> T buildService(Class<T> serviceInterface) {
        return (T) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { serviceInterface },
                dispatcher);
    }

}
