package org.jumpmind.pos.service.startup;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jumpmind.pos.service.IModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ModuleRegistry {

    final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    List<IModule> modules;
    
    public ModuleRegistry() {
       logger.info("Creating module registry");
    }

    @PostConstruct
    public void loadModuleDatabaseDefaults() {
        if (this.modules != null) {
            BufferedWriter out = null;
            Date date = new Date();

            try {
                File file = new File(".h2.server.properties");
                out = new BufferedWriter(new FileWriter(file));
                out.write("#H2 Server Properties\n#" + DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(date) + "\n");

                int pos = 0;
                if (out != null) {
                    for (IModule module : modules) {
                        try {
                            out.write(pos + "=" + StringUtils.capitalize(module.getName()) + "|");
                            out.write(module.getDriver() + "|");
                            out.write(module.getURL().replace("jdbc:openpos:", "jdbc:") + "|\n");
                            pos++;
                        } catch (IOException e) {
                            logger.warn("Unable to configure " + module.getName() + " in \".h2.server.properties\"");
                        }
                    }
                }
            } catch (IOException e) {
                logger.warn("Unable to configure \".h2.server.properties\" file");
            } finally {
                IOUtils.closeQuietly(out);
            }
        }
    }

    public List<IModule> getModules() {
        return modules;
    }

}
