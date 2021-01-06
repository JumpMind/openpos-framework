package org.jumpmind.pos.test;

import org.jumpmind.pos.util.clientcontext.ClientContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ClientContextTestUtils {

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
