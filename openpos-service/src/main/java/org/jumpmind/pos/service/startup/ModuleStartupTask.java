package org.jumpmind.pos.service.startup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.jumpmind.pos.service.IModule;
import org.jumpmind.pos.util.BoxLogging;
import org.jumpmind.pos.util.startup.AbstractStartupTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(100)
public class ModuleStartupTask extends AbstractStartupTask {

    @Autowired
    ModuleRegistry moduleRegistry;

    @Override
    protected void doTask() throws Exception {
        if (moduleRegistry.getModules() != null && moduleRegistry.getModules().size() > 0) {
            List<IModule> modules = new ArrayList<>(moduleRegistry.getModules());
            Iterator<IModule> i = modules.iterator();
            while (i.hasNext()) {
                IModule module = i.next();
                try {
                    logger.info(logMessage("Initializing module: ", module));
                    module.initialize();
                } catch (Exception ex) {
                    logger.error(logMessage("Failed to initialize module: ", module), ex);
                    i.remove();
                }
            }
            for (IModule module : modules) {
                try {
                    logger.info(logMessage("Started module: ", module));
                    module.start();
                } catch (Exception ex) {
                    logger.error(logMessage("Failed to start module: ", module), ex);
                }
            }

        } else {
            logger.info(BoxLogging.box("No modules detected to start ..."));
        }
    }

    private String logMessage(String msg, IModule module) {
        return BoxLogging.box(msg + StringUtils.leftPad(module.getName(), 15).toUpperCase());
    }

}
