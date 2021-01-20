package org.jumpmind.pos.persist.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AugmenterConfigsTest {

    private AugmenterConfigs augmenterConfigs;
    private List<AugmenterConfig> configs;

    @Before
    public void setUp() throws Exception {
        augmenterConfigs = new AugmenterConfigs();

        configs = new ArrayList<>();
        AugmenterConfig config1 = new AugmenterConfig();
        config1.setName("options");
        config1.setPrefix("OPTION_");
        List<AugmenterModel> augmenters1 = new ArrayList<>();
        AugmenterModel augmenter = new AugmenterModel();
        augmenter.setName("size");
        augmenters1.add(augmenter);
        augmenter = new AugmenterModel();
        augmenter.setName("style");
        augmenters1.add(augmenter);
        config1.setAugmenters(augmenters1);
        configs.add(config1);
        AugmenterConfig config2 = new AugmenterConfig();
        config2.setName("classifiers");
        config2.setPrefix("CLASSIFIER_");
        List<AugmenterModel> augmenters2 = new ArrayList<>();
        augmenter = new AugmenterModel();
        augmenter.setName("department");
        augmenters2.add(augmenter);
        configs.add(config2);
        augmenterConfigs.setConfigs(configs);
    }

    @Test
    public void testGetConfigsNames() {
        assertEquals(Arrays.asList("options", "classifiers"), augmenterConfigs.getConfigsNames());
    }

    @Test
    public void testGetConfigsByNamesWhenNoNamesMatch() {
        assertEquals(Collections.emptyList(), augmenterConfigs.getConfigsByNames("test", "test2"));
    }

    @Test
    public void testGetConfigsByNamesWhenSingleNameMatches() {
        assertEquals(Arrays.asList(configs.get(0)), augmenterConfigs.getConfigsByNames("test", "options"));
    }

    @Test
    public void testGetConfigsByNamesWhenAllNamesMatch() {
        assertEquals(Arrays.asList(configs.get(0), configs.get(1)), augmenterConfigs.getConfigsByNames("classifiers", "options"));
    }

    @Test
    public void testGetConfigByNameReturnNullWhenNoMatchingName() {
        assertNull(augmenterConfigs.getConfigByName("nothing"));
    }

    @Test
    public void testGetConfigByNameReturnsMatchingConfig() {
        assertEquals(configs.get(1), augmenterConfigs.getConfigByName("classifiers"));
    }
}