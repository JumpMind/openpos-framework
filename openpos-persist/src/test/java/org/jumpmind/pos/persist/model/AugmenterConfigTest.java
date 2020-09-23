package org.jumpmind.pos.persist.model;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class AugmenterConfigTest {

    private AugmenterConfig augmenterConfig;
    private List<AugmenterModel> augmenters;

    @Before
    public void setUp() {
        this.augmenterConfig = new AugmenterConfig();
        augmenterConfig.setName("junit");
        augmenterConfig.setPrefix("JUNIT_");
        augmenters = new ArrayList<>();
        AugmenterModel augmenter1 = new AugmenterModel();
        augmenter1.setName("aug1");
        augmenters.add(augmenter1);
        AugmenterModel augmenter2 = new AugmenterModel();
        augmenter2.setName("aug2");
        augmenters.add(augmenter2);
        augmenterConfig.setAugmenters(augmenters);
    }

    @Test
    public void testGetAugmenterReturnsNullIfMissingAugmenterName() {
        assertNull(augmenterConfig.getAugmenter("missing"));
    }

    @Test
    public void testGetAugmenterReturnsCorrectAugmenter() {
        assertEquals(augmenters.get(0), augmenterConfig.getAugmenter("aug1"));
    }

    @Test
    public void testGetAugmenterNamesReturnsListOfNames() {
        assertEquals(Arrays.asList("aug1", "aug2"), augmenterConfig.getAugmenterNames());
    }
}