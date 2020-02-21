package org.jumpmind.pos.util.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@ToString
@Data
public class AppEvent extends Event implements Serializable {

    String deviceId;
    String appId;
    boolean remote;

    public AppEvent() {
      super();
    }

    public AppEvent(String deviceId, String appId) {
        super(createSourceString(appId, deviceId));
        this.deviceId = deviceId;
        this.appId = appId;
    }

    public static String createSourceString(String appId, String deviceId) {
        return appId + "/" + deviceId;
    }

}
