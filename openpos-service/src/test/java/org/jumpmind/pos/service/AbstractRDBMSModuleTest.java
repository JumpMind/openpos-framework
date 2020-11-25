package org.jumpmind.pos.service;


import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AbstractRDBMSModuleTest {

    AbstractRDBMSModule abstractRDBMSModule;

    ModuleLoaderConfig config;

    @Before
    public void setup() {
        abstractRDBMSModule = new AbstractRDBMSModule() {
            @Override
            protected String getArtifactName() {
                return null;
            }

            @Override
            public String getName() {
                return "test";
            }

            @Override
            public String getTablePrefix() {
                return null;
            }
        };

        config = new ModuleLoaderConfig();
        abstractRDBMSModule.setLoaderConfig(config);
    }

    @Test
    public void testIsDatabaseUpgradeableWhenMissingConfig() {
        abstractRDBMSModule.setLoaderConfig(null);
        assertTrue(abstractRDBMSModule.isDatabaseUpgradeable());
    }

    @Test
    public void testIsDatabaseUpgradeableWhenConfigIsInIncludes() {
        config.setIncludes(new LinkedHashSet<>(Arrays.asList("test")));
        assertTrue(abstractRDBMSModule.isDatabaseUpgradeable());
    }

    @Test
    public void testIsDatabaseUpgradeableWhenConfigIsNotInIncludes() {
        config.setIncludes(new LinkedHashSet<>(Arrays.asList("nottest")));
        assertFalse(abstractRDBMSModule.isDatabaseUpgradeable());
    }

    @Test
    public void testIsDatabaseUpgradeableWhenConfigIsInExcludes() {
        config.setExcludes(new LinkedHashSet<>(Arrays.asList("test")));
        assertFalse(abstractRDBMSModule.isDatabaseUpgradeable());
    }

    @Test
    public void testIsDatabaseUpgradeableWhenConfigIsNotInExcludes() {
        config.setExcludes(new LinkedHashSet<>(Arrays.asList("nottest")));
        assertTrue(abstractRDBMSModule.isDatabaseUpgradeable());
    }

    @Test
    public void testIsDatabaseUpgradeableWhenConfigWhenInBoth() {
        config.setExcludes(new LinkedHashSet<>(Arrays.asList("test")));
        config.setIncludes(new LinkedHashSet<>(Arrays.asList("test")));
        assertTrue(abstractRDBMSModule.isDatabaseUpgradeable());
    }

    @Test
    public void testIsDatabaseUpgradeableWhenConfigWhenNotInBoth() {
        config.setExcludes(new LinkedHashSet<>(Arrays.asList("nottest")));
        config.setIncludes(new LinkedHashSet<>(Arrays.asList("nottest")));
        assertFalse(abstractRDBMSModule.isDatabaseUpgradeable());
    }
}