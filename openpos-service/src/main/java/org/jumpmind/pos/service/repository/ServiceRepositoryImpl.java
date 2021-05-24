package org.jumpmind.pos.service.repository;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class ServiceRepositoryImpl<KEY, SERVICE> implements ServiceRepository<KEY, SERVICE> {

    private ConcurrentHashMap<KEY, SERVICE> serviceMap;

    @Override
    public boolean hasService(KEY key) {
        return serviceMap.containsKey(key);
    }

    @Override
    public SERVICE getService(KEY key) {
        if (!serviceMap.containsKey(key)) {
            throw new IllegalStateException("Unregistered service key " + key + " in repository " + this.getClass().getName());
        }
        return serviceMap.get(key);
    }

    @Override
    public Collection<SERVICE> getAllServices() {
        return serviceMap.values();
    }

    protected void setServices(List<SERVICE> services, Function<SERVICE, KEY> keyProducer) {
        Map<KEY, SERVICE> serviceMap = new HashMap<>(services.size());
        for (SERVICE service : services) {
            SERVICE replaced = serviceMap.put(keyProducer.apply(service), service);
            if (replaced != null) {
                throw new IllegalStateException("Repository " + this.getClass() + " has several services with same key: " + service.getClass() + ", " + replaced.getClass());
            }
        }
        setServiceMap(serviceMap);
    }

    protected void setServiceMap(Map<KEY, SERVICE> serviceMap) {
        this.serviceMap = new ConcurrentHashMap<>(serviceMap);
    }
}
