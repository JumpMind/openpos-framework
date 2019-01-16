package org.jumpmind.pos.devices.model;

import java.util.ArrayList;
import java.util.List;

import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name = "config")
public class DeviceConfigModel extends AbstractModel {

    @ColumnDef(primaryKey = true)
    String profile;

    @ColumnDef(primaryKey = true)
    String deviceName;

    @ColumnDef(size = "255")
    String description;

    @ColumnDef
    String serviceClass;

    @ColumnDef
    String factoryClass;

    List<DeviceConfigPropModel> properties = new ArrayList<>();

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceClass() {
        return serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getFactoryClass() {
        return factoryClass;
    }

    public void setFactoryClass(String factoryClass) {
        this.factoryClass = factoryClass;
    }

    public void setProperties(List<DeviceConfigPropModel> properties) {
        this.properties = properties;
    }

    public List<DeviceConfigPropModel> getProperties() {
        return properties;
    }

    public void add(DeviceConfigPropModel prop) {
        this.properties.add(prop);
    }

}
