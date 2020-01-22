package org.jumpmind.pos.devices.model;

import lombok.Data;
import org.jumpmind.pos.persist.AbstractModel;
import org.jumpmind.pos.persist.ColumnDef;
import org.jumpmind.pos.persist.IndexDef;
import org.jumpmind.pos.persist.TableDef;

@Data
@TableDef(name = "device_auth")
public class DeviceAuthModel extends AbstractModel {
    @ColumnDef(primaryKey = true)
    private String deviceId;

    @ColumnDef(primaryKey = true)
    private String appId;

    @ColumnDef()
    @IndexDef(name="idx_devices_auth_token")
    private String authToken;
}
