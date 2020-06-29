package org.jumpmind.pos.hazelcast;

import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceNotActiveException;
import lombok.extern.slf4j.Slf4j;
import org.jumpmind.pos.core.event.DeviceHeartbeatEvent;
import org.jumpmind.pos.util.event.AppEvent;
import org.jumpmind.pos.util.event.ITransientEvent;
import org.jumpmind.pos.util.model.DeviceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

@Component
@Profile("hazelcast")
@Slf4j
public class DeviceStatusMapHazelcastImpl implements IDeviceStatusMap, MembershipListener {
    private static final String DEVICES_MAP_NAME = "devices-map";

    @Autowired
    DistributedMapProvider mapProvider;

    @Autowired
    HazelcastInstance hz;
    
    Consumer<String> disappearanceHandler;

    @PostConstruct
    void init() {
        hz.getCluster().addMembershipListener(this);
    }
    
    @Override
    public ConcurrentMap<String, DeviceStatus> get() {
        try {
            return mapProvider.getMap(DEVICES_MAP_NAME, String.class, DeviceStatus.class);
        } catch (HazelcastInstanceNotActiveException e) {
            log.info("Hazelcast was not active.  This is probably because we are shutting down.  Returning a dummy map");
            return new ConcurrentHashMap<>();
        }
    }

    @EventListener(classes = AppEvent.class)
    protected void updateDeviceStatus(AppEvent event) {
        if (!event.isRemote() && !(event instanceof ITransientEvent)) {
            update(event);
        }
    }
    
    @EventListener(classes = DeviceHeartbeatEvent.class)
    protected void processHeartbeat(DeviceHeartbeatEvent event) {
        if (! event.isRemote()) {
            touch(event.getDeviceId());
        }
    }
    
    @Override
    public synchronized void touch(String deviceId) {
        // FYI: map.compute() method doesn't work with hazelcast or I'd use it.
        DeviceStatus status = get().get(deviceId);
        if (status == null) {
            status = new DeviceStatus(deviceId, hz.getLocalEndpoint().getUuid().toString());
        } else {
            status = status.shallowCopy();
            status.setLastActiveTimeMs(System.currentTimeMillis());
        }
        get().put(deviceId, status);

    }

    @Override
    public synchronized void update(AppEvent event) {
        // FYI: map.compute() method doesn't work with hazelcast or I'd use it.
        if (event.getDeviceId() != null) {
            DeviceStatus status = get().get(event.getDeviceId());
            if (status == null) {
                status = new DeviceStatus(event.getDeviceId(), hz.getLocalEndpoint().getUuid().toString());
            } else {
                status = status.shallowCopy();
            }
            status.setLastActiveTimeMs(System.currentTimeMillis());
            status.setLatestEvent(event);

            get().put(event.getDeviceId(), status);
        } else {
            log.warn("Received an event with a null device id.  {}", event);
        }
    }

    @Override
    public void setDisappearanceHandler(Consumer<String> onDeviceDisconnect) {
        this.disappearanceHandler = onDeviceDisconnect;
    }

    @Override
    public void memberAdded(MembershipEvent event) {
        log.trace("New member added with id: {}", event.getMember().getUuid().toString());
    }

    @Override
    public void memberRemoved(MembershipEvent event) {
        if (this.disappearanceHandler != null) {
            String memberId = event.getMember().getUuid().toString();
            get().values().stream()
                .filter(deviceStatus -> memberId.equals(deviceStatus.getServerId()))
                .forEach(deviceStatus -> {
                    this.disappearanceHandler.accept(deviceStatus.getDeviceId());
                });
        }
    }

}
