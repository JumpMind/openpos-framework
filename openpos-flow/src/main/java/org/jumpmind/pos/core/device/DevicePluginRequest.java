package org.jumpmind.pos.core.device;

public class DevicePluginRequest extends DefaultDeviceRequest implements IDevicePlugin {

    private static final long serialVersionUID = 1L;
    private String pluginId;

    public DevicePluginRequest(String pluginId, String deviceId, Object payload) {
        this(pluginId, deviceId, null, payload);
    }
    
    public DevicePluginRequest(String pluginId, String deviceId, String subType, Object payload) {
        super(deviceId, subType, payload);
        this.pluginId = pluginId;
    }

    @Override
    public String getPluginId() {
        return this.pluginId;
    }

    @Override
    public void setPluginId(String pluginId) {
        this.pluginId = pluginId;
    }

}
