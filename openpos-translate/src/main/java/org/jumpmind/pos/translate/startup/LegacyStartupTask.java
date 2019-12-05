package org.jumpmind.pos.translate.startup;

import org.jumpmind.pos.translate.ILegacyStartupService;
import org.jumpmind.pos.util.startup.AbstractStartupTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


@Component
@Order(500)
public class LegacyStartupTask extends AbstractStartupTask {

    @Autowired
    ILegacyStartupService legacyStartupService;

    @Override
    protected void doTask() throws Exception {
        legacyStartupService.start();
    }
}
