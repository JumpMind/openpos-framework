package org.jumpmind.pos.devices.model;

import lombok.Data;
import org.jumpmind.pos.persist.*;

@Data
@TableDef(name = "device_auth")
@IndexDefs({
        @IndexDef(name = "idx_devices_auth_token", column = "authToken")
})
public class DeviceAuthModel extends AbstractModel {
    @ColumnDef(primaryKey = true)
    private String deviceId;

    @ColumnDef(primaryKey = true)
    private String appId;

    @ColumnDef()
    private String authToken;
}
