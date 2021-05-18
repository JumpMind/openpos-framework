package org.jumpmind.pos.util.startup;

import static java.lang.Integer.MAX_VALUE;

import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jumpmind.pos.util.BoxLogging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(MAX_VALUE)
public class LastStartupTask extends AbstractStartupTask {    
        
    @Autowired
    MutableBoolean initialized;
    
    @Value("${server.port:?}")
    String serverPort;

    @Value("${secured.port:?}")
    String securedPort;

    @Value("${secured.enabled:false}")
    boolean securedEnabled;

    @Override
    protected void doTask() throws Exception {
        initialized.setTrue();
        String msg = String.format("Server started on port %s (http)", serverPort);
        if (securedEnabled) {
            msg += String.format(" and on port %s (https)", securedPort);
        }
        logger.info(BoxLogging.box(msg));
    }
    
}
