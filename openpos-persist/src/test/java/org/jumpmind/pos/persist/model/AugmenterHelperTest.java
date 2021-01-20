package org.jumpmind.pos.persist.model;

import org.jumpmind.pos.persist.Augmented;
import org.jumpmind.pos.persist.cars.AugmentedCarModel;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class AugmenterHelperTest {

    AugmenterHelper augmenterHelper;

    @Before
    public void setup() {
        augmenterHelper = new AugmenterHelper();
        AugmenterConfigs augmenterConfigs = new AugmenterConfigs();
        augmenterHelper.setAugmenterConfigs(augmenterConfigs);
        List<AugmenterConfig> augmenterConfigList = new ArrayList<>();
        AugmenterConfig augmenterConfig = new AugmenterConfig();
        augmenterConfig.setName("options");
        augmenterConfig.setPrefix("OPTION_");
        augmenterConfigList.add(augmenterConfig);
        List<AugmenterModel> augmenters = new ArrayList<>();
        AugmenterModel augmenter = new AugmenterModel();
        augmenter.setName("color");
        augmenters.add(augmenter);
        AugmenterModel augmenter2 = new AugmenterModel();
        augmenter2.setName("size");
        augmenter2.setDefaultValue("medium");
        augmenters.add(augmenter2);
        augmenterConfig.setAugmenters(augmenters);
        augmenterConfigs.setConfigs(augmenterConfigList);
    }

    @Augmented(name = "missing_config")
    static class MockAugmentModel extends AbstractAugmentedModel {
        String id;
    }

    @Test
    public void testGetAugmenterConfigs() {
        assertNotNull(augmenterHelper.getAugmenterConfigs());
    }

    @Test
    public void testGetAugmenterConfigByNameWhenConfigExists() {
        AugmenterConfig augmenterConfig = augmenterHelper.getAugmenterConfig("options");
        assertNotNull(augmenterConfig);
        assertEquals("options", augmenterConfig.getName());;
    }

    @Test
    public void testGetAugmenterConfigByNameWhenConfigDoesNotExist() {
        assertNull(augmenterHelper.getAugmenterConfig("name"));
    }

    @Test
    public void testGetAugmenterConfigByObjectWhenObjectIsAugmented() {
        List<AugmenterConfig> augmenterConfigs = augmenterHelper.getAugmenterConfigs(new AugmentedCarModel());
        assertNotNull(augmenterConfigs);
        assertEquals(1, augmenterConfigs.size());
        assertEquals("options", augmenterConfigs.get(0).getName());
    }

    @Test
    public void testGetAugmenterConfigByObjectWhenObjectIsNotAugmented() {
        AugmenterConfig augmenterConfig = augmenterHelper.getAugmenterConfig("Test");
        assertNull(augmenterConfig);
    }

    @Test
    public void testGetAugmenterConfigByClassWhenClassIsAugmented() {
        List<AugmenterConfig> augmenterConfigs = augmenterHelper.getAugmenterConfigs(AugmentedCarModel.class);
        assertNotNull(augmenterConfigs);
        assertEquals(1, augmenterConfigs.size());
        assertEquals("options", augmenterConfigs.get(0).getName());
    }

    @Test
    public void testGetAugmenterConfigByClassWhenClassIsNotAugmented() {
        List<AugmenterConfig> augmenterConfigs = augmenterHelper.getAugmenterConfigs(String.class);
        assertEquals(0, augmenterConfigs.size());
    }

    @Test
    public void testAddAugmentsAddsAugmentsToModelWhenConfigured() {
        IAugmentedModel augmentedModel = new AugmentedCarModel();
        assertTrue(augmentedModel.getAugments().isEmpty());
        Map<String, Object> augmentsMap = new HashMap<>();
        augmentsMap.put("OPTION_COLOR", "red");
        augmenterHelper.addAugments(augmentedModel, augmentsMap);
        assertEquals(1, augmentedModel.getAugments().size());
        assertEquals("red", augmentedModel.getAugmentValue("color"));
    }

    @Test
    public void testAddAugmentsAddsAugmentsToModelWhenColumnNotMapped() {
        IAugmentedModel augmentedModel = new AugmentedCarModel();
        assertTrue(augmentedModel.getAugments().isEmpty());
        Map<String, Object> augmentsMap = new HashMap<>();
        augmentsMap.put("COLOR", "red");
        augmenterHelper.addAugments(augmentedModel, augmentsMap);
        assertEquals(0, augmentedModel.getAugments().size());
    }

    @Test
    public void testGetDefaultValueWhenDefaultExists() {
        AugmentedCarModel augmentedModel = new AugmentedCarModel();
        assertEquals("medium", augmenterHelper.getDefaultValue("OPTION_SIZE", augmentedModel));
    }

    @Test
    public void testGetDefaultValueWhenDefaultValuesDoesNotExist() {
        AugmentedCarModel augmentedModel = new AugmentedCarModel();
        assertNull(augmenterHelper.getDefaultValue("OPTION_COLOR", augmentedModel));
    }

    @Test
    public void testGetDefaultValueWhenFieldNameIsInvalid() {
        AugmentedCarModel augmentedModel = new AugmentedCarModel();
        assertNull(augmenterHelper.getDefaultValue("", augmentedModel));
    }

    @Test
    public void testGetParametersForAugmentedModelWhenConfigurationExists() {
        AugmentedCarModel augmentedCarModel = new AugmentedCarModel();
        augmentedCarModel.setAugmentValue("color", "blue");
        augmentedCarModel.setAugmentValue("size", "large");
        Map<String, Object> params = augmenterHelper.getParametersForAugmentedModel(augmentedCarModel, AugmentedCarModel.class);
        assertEquals(4, params.size());
        assertEquals("option_color", params.get("augment1ColumnName").toString().toLowerCase());
        assertEquals("blue", params.get("augment1Value"));
        assertEquals("option_size", params.get("augment2ColumnName").toString().toLowerCase());
        assertEquals("large", params.get("augment2Value"));
    }

    @Test
    public void testGetParametersForAugmentedModelWhenNoConfigExists() {
        IAugmentedModel augmentedModel = new MockAugmentModel();
        Map<String, Object> params = augmenterHelper.getParametersForAugmentedModel(augmentedModel, MockAugmentModel.class);
        assertEquals(0, params.size());
    }
}