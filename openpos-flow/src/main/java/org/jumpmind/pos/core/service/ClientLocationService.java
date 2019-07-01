package org.jumpmind.pos.core.service;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("device")
public class ClientLocationService {

    private LocationData locationData;

    private List<ILocationListener> listeners = new ArrayList<>();

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
        for (ILocationListener l: this.listeners) {
            l.locationChanged(locationData);
        }
    }

    public void addLocationListener(ILocationListener listener) {
        if (!this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }


}
