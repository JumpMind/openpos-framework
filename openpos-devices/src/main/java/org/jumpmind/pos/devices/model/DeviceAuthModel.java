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

    @ColumnDef()
    private String authToken;

    @Override
    public int compareTo(Object o) {
        if (o instanceof DeviceAuthModel) {
            DeviceAuthModel da2 = (DeviceAuthModel)o;
            return deviceId.compareTo(da2.getDeviceId());
        } else {
            return -1;
        }
    }
}
