package org.jumpmind.pos.core.ui.data;

import java.beans.PropertyChangeEvent;

public class UIDataMessageProviderPropertyChangeEvent extends PropertyChangeEvent {

    private String appId;
    private String deviceId;

    /**
     * Constructs a new {@code PropertyChangeEvent}.
     *
     * @param source       the bean that fired the event
     * @param propertyName the programmatic name of the property that was changed
     * @param oldValue     the old value of the property
     * @param newValue     the new value of the property
     * @throws IllegalArgumentException if {@code source} is {@code null}
     */
    public UIDataMessageProviderPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue) {
        super(source, propertyName, oldValue, newValue);
    }

    public UIDataMessageProviderPropertyChangeEvent(Object source, String propertyName, String appId, String deviceId) {
        super(source, propertyName, null, null);
        this.appId = appId;
        this.deviceId = deviceId;
    }

    public UIDataMessageProviderPropertyChangeEvent(Object source, String propertyName, Object oldValue, Object newValue, String appId, String deviceId) {
        super(source, propertyName, oldValue, newValue);
        this.appId = appId;
        this.deviceId = deviceId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppId() {
        return appId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
