package org.jumpmind.pos.devices.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jumpmind.pos.persist.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableDef(name = "device_auth",
        primaryKey = {"deviceId"})
@IndexDefs({
        @IndexDef(name = "idx_devices_auth_token", column = "authToken")
})
public class DeviceAuthModel extends AbstractModel implements Comparable {
    @ColumnDef
    private String deviceId;

    @ColumnDef
    private String appId;

    @ColumnDef()
    private String authToken;

    @Override
    public int compareTo(Object o) {
        if (o instanceof DeviceAuthModel) {
            DeviceAuthModel da2 = (DeviceAuthModel)o;
            String id2 = da2.getDeviceId() + ":" + da2.getAppId();
            String id1 = getDeviceId() + ":" + getAppId();
            return id1.compareTo(id2);
        } else {
            return -1;
        }
    }
}
