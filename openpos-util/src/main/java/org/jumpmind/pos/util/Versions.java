package org.jumpmind.pos.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Component
public class Versions {

    static List<Version> versions = new ArrayList<>();

    static final Logger log = LoggerFactory.getLogger(Versions.class);

    List<InputStream> loadResources(final String name) throws IOException {
        final List<InputStream> list = new ArrayList<InputStream>();
        Resource[] resources = ResourceUtils.getResources("openpos-version.properties");
        if (resources != null) {
            for (Resource resource : resources) {
                list.add(resource.getInputStream());
            }
        }
        return list;
    }

    @PostConstruct
    protected synchronized void init() {
        try {
            List<Version> building = new ArrayList<>();
            List<InputStream> resources = loadResources("openpos-version.properties");
            log.info(BoxLogging.box("Versions"));
            for (InputStream is : resources) {
                Properties properties = new Properties();
                properties.load(is);
                ObjectMapper m = DefaultObjectMapper.build();
                Version version = m.convertValue(properties, Version.class);
                log.info(m.writerWithDefaultPrettyPrinter().writeValueAsString(version));
                building.add(version);
            }
            versions = building;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static List<Version> getVersions() {
        return versions;
    }

    @Override
    public String toString() {
        return versions();
    }

    public static String versions() {
        StringBuilder b = new StringBuilder();
        if (versions.size() > 0) {
            b.append("\n*******************************************************\n");
            b.append("Versions:\n");
            for (Version version : versions) {
                b.append(version.toString()).append("\n");
            }
            b.append("*******************************************************\n");
        }
        return b.toString();
    }
}
