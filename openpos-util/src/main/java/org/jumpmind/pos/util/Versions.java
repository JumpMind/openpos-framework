package org.jumpmind.pos.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class Versions {

    static List<Version> versions = new ArrayList<>();

    static final Logger log = LoggerFactory.getLogger(Versions.class);

    protected static List<InputStream> loadResources(final String name, final ClassLoader classLoader) throws IOException {
        final List<InputStream> list = new ArrayList<InputStream>();
        final Enumeration<URL> systemResources = (classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader).getResources(name);
        while (systemResources.hasMoreElements()) {
            list.add(systemResources.nextElement().openStream());
        }
        return list;
    }

    @PostConstruct
    protected synchronized void init() {
        try {
            List<Version> building = new ArrayList<>();
            List<InputStream> resources = loadResources("openpos-version.properties", Thread.currentThread().getContextClassLoader());
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
