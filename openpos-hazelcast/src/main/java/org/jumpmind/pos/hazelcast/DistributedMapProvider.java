package org.jumpmind.pos.hazelcast;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
@Profile("hazelcast")
public class DistributedMapProvider {
    @Autowired
    HazelcastInstance hz;

    /**
     * Returns a new or existing distributed map associated with the given name.  The map is replicated across all
     * members of the hazelcast cluster.
     * @param name The name of the map to retrieve or create.
     * @param keyType The expected data type of the keys in the map.
     * @param valueType The expected data type of the values in the map.
     * @param <K> Type of the keys
     * @param <V> Type of the values
     * @return A new or existing map associated with the given name.
     */
    public <K,V> ConcurrentMap<K,V> getMap(String name, Class<K> keyType, Class<V> valueType) {
        try {
            return hz.getMap(name);
        } catch (HazelcastInstanceNotActiveException ex) {
            log.info("Hazelcast was not active.  This is probably because we are shutting down.  Returning a dummy map");
            return new ConcurrentHashMap<>();
        }
    }
}
