package org.jumpmind.pos.core.device;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.jumpmind.pos.util.event.AppEvent;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class DeviceStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private String serverId;
    private String deviceId;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property="__class")
    private AppEvent latestEvent;
    private long lastActiveTimeMs;
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property="__class")
    private Object payload;

    protected DeviceStatus(){};

    public DeviceStatus(String deviceId) {
        this.deviceId = deviceId;
        this.lastActiveTimeMs = System.currentTimeMillis();
    }

    public DeviceStatus(String deviceId, String serverId) {
        this(deviceId);
        this.serverId = serverId;
    }

    public DeviceStatus shallowCopy() {
        DeviceStatus copy = new DeviceStatus(this.deviceId);
        copy.serverId = this.serverId;
        copy.latestEvent = this.latestEvent;
        copy.lastActiveTimeMs = this.lastActiveTimeMs;
        copy.payload = this.payload;

        return copy;
    }
}
