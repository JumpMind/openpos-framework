package org.jumpmind.pos.devices;

import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DeviceTestUtils {

    @Autowired
    private ClientContext clientContext;

    public void setTestDevice( String deviceId, String appId ){
        clientContext.put("deviceId", deviceId);
        clientContext.put( "appId", appId);
    }

    public void setTimezoneOffset(String offset){
        clientContext.put("timezoneOffset", offset);
    }

    public void reset(){
        clientContext.clear();
    }
}
