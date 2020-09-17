package org.jumpmind.pos.devices.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.persist.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableDef(name = "device_auth",
        primaryKey = {"deviceId", "appId"})
@IndexDefs({
        @IndexDef(name = "idx_devices_auth_token", column = "authToken")
})
public class DeviceAuthModel extends AbstractModel {
    @ColumnDef
    private String deviceId;

    @ColumnDef
    private String appId;

    @ColumnDef()
    private String authToken;
}
