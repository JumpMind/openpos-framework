package org.jumpmind.pos.devices.model;

import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.TableDef;

@TableDef(name="config_prop")
public class DeviceConfigPropModel extends AbstractModel {

    private static final long serialVersionUID = 1L;

    @ColumnDef(primaryKey=true)
    String profile;
    
    @ColumnDef(primaryKey=true)
    String deviceName;
    
    @ColumnDef(primaryKey=true)
    String propertyName;
    
    @ColumnDef
    String propertyValue;
    
    @ColumnDef
    String propertyType;

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
    
    public String getPropertyType() {
        return propertyType;
    }
    
}
