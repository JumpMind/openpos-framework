package org.jumpmind.pos.context.model;

import org.jumpmind.pos.persist.cars.TestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestConfig.class})
public class ContextRepositoryTest {
    
    @Autowired
    ContextRepository contextRepository;
    
    
    
//    @BeforeClass
//    public static void initDB() {
//        System.setProperty("module.context.init.sql", "/context-test-data.sql");
//    }
    

    @Before 
    public void before() throws Exception {        
//        ContextTestUtils.initTestDB(contextRepository, "/context-test-data.sql");
    }
    
    @Test 
    public void testLoadTags() throws Exception {
//        List<TagModel> tags = contextRepository.loadTags();
//        assertEquals(tags.size(), 7);
//        
//        assertEquals(tags.get(0).getTagName(), "app_profile");
//        assertEquals(tags.get(3).getTagName(), "state");
//        assertEquals(tags.get(4).getTagName(), "country");
//        assertEquals(tags.get(5).getTagName(), "region");
    }
//    
//    @Test
//    public void testSimpleConfigLoad() {
//        ConfigModel config = contextRepository.findConfigValue(new Date(), ContextTestUtils.getTestNodeTags(), "config.string");
//        assertEquals("ALL string value", config.getConfigValue());
//    }
//    @Test
//    public void testIntConfigLoad() {
//        ConfigModel config = contextRepository.findConfigValue(new Date(), ContextTestUtils.getTestNodeTags(), "config.int");
//        assertEquals("500", config.getConfigValue());
//    }
//    @Test
//    public void testConfigEnabled() {
//        ConfigModel config = contextRepository.findConfigValue(new Date(), ContextTestUtils.getTestNodeTags(), "config.enabled.test");
//        assertEquals("this one is ENABLED.", config.getConfigValue());
//    }
//    @Test
//    public void testEffectiveExpireDates() {
//        ConfigModel config = contextRepository.findConfigValue(new Date(), ContextTestUtils.getTestNodeTags(), "config.effective.test");
//        assertEquals("this one should be active.", config.getConfigValue());
//    }
//    
//    @Test(expected = ContextException.class)
//    public void testMostSpecificConfigNullValues1() {
//        ContextRepository contextRepo = new ContextRepository();
//        contextRepo.findMostSpecificConfig(null, null);
//    }
//    @Test(expected = ContextException.class)
//    public void testMostSpecificConfigNullValues2() {
//        ContextRepository contextRepo = new ContextRepository();
//        contextRepo.findMostSpecificConfig(new HashMap<>(), null);
//    }
//    @Test(expected = ContextException.class)
//    public void testMostSpecificConfigNullValues3() {
//        ContextRepository contextRepo = new ContextRepository();
//        contextRepo.findMostSpecificConfig(null, new ArrayList<>());
//    }
//
//    @Test
//    public void testMostSpecificConfig1() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_REGION, "NAM");
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setLocationType(LocationType.REGION);
//            config.setLocationValue("NAM");
//            config.setConfigName("test.config");
//            config.setConfigValue("NAM_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setBrandId("JUSTICE");
//            config.setConfigName("test.config");
//            config.setConfigValue("JUSTICE_VALUE");
//            configs.add(config);            
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("JUSTICE_VALUE", config.getConfigValue());
//    }
//    
//    @Test
//    public void testMostSpecificConfig2() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_REGION, "NAM");
//        tags.put(ContextRepository.TAG_STORE, "101");
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {   
//            ConfigModel config = new ConfigModel();
//            config.setLocationType(LocationType.REGION);
//            config.setLocationValue("NAM");
//            config.setConfigName("test.config");
//            config.setConfigValue("NAM_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setLocationType(LocationType.STORE);
//            config.setLocationValue("101");
//            config.setConfigName("test.config");
//            config.setConfigValue("101_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setBrandId("JUSTICE");
//            config.setConfigName("test.config");
//            config.setConfigValue("JUSTICE_VALUE");
//            configs.add(config);            
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setLocationType(LocationType.STORE);
//            config.setLocationValue("101");
//            config.setBrandId("JUSTICE");
//            config.setConfigName("test.config");
//            config.setConfigValue("101_JUSTICE_VALUE");
//            configs.add(config);            
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("101_JUSTICE_VALUE", config.getConfigValue());
//    }
//    
//    
//    @Test
//    public void testChooseCorrectConfig() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_REGION, "AMEA");
//        tags.put(ContextRepository.TAG_STORE, "101");
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {   
//            ConfigModel config = new ConfigModel();
//            config.setLocationType(LocationType.REGION);
//            config.setLocationValue("NAM");
//            config.setConfigName("test.config");
//            config.setConfigValue("NAM_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setConfigName("test.config");
//            config.setConfigValue("*_VALUE");
//            configs.add(config);
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("*_VALUE", config.getConfigValue());
//    }
//    
//    @Test
//    public void testTieBreak1() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        tags.put(ContextRepository.TAG_DEPARTMENT_ID, "FOOTWEAR");
//        tags.put(ContextRepository.TAG_STORE_TYPE, "OUTLET");
//        tags.put(ContextRepository.TAG_DEVICE_TYPE, "MOBILE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {   
//            ConfigModel config = new ConfigModel();
//            config.setBrandId("JUSTICE");
//            config.setConfigName("test.config");
//            config.setConfigValue("JUSTICE_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setDepartmentId("FOOTWEAR");
//            config.setConfigName("test.config");
//            config.setConfigValue("FOOTWEAR_VALUE");
//            configs.add(config);
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("JUSTICE_VALUE", config.getConfigValue());
//    }
//    
//    
//    @Test
//    public void testTieBreak2() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        tags.put(ContextRepository.TAG_DEPARTMENT_ID, "FOOTWEAR");
//        tags.put(ContextRepository.TAG_STORE_TYPE, "OUTLET");
//        tags.put(ContextRepository.TAG_DEVICE_TYPE, "MOBILE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setDepartmentId("FOOTWEAR");
//            config.setConfigName("test.config");
//            config.setConfigValue("FOOTWEAR_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setStoreType("OUTLET");
//            config.setConfigName("test.config");
//            config.setConfigValue("OUTLET_VALUE");
//            configs.add(config);
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("OUTLET_VALUE", config.getConfigValue());
//    }    
//    
//    @Test
//    public void testTieBreak3() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        tags.put(ContextRepository.TAG_DEPARTMENT_ID, "FOOTWEAR");
//        tags.put(ContextRepository.TAG_STORE_TYPE, "OUTLET");
//        tags.put(ContextRepository.TAG_DEVICE_TYPE, "MOBILE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {   
//            ConfigModel config = new ConfigModel();
//            config.setBrandId("JUSTICE");
//            config.setConfigName("test.config");
//            config.setConfigValue("JUSTICE_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setStoreType("OUTLET");
//            config.setConfigName("test.config");
//            config.setConfigValue("OUTLET_VALUE");
//            configs.add(config);
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("JUSTICE_VALUE", config.getConfigValue());
//    }    
//    
//    @Test
//    public void testTieBreak4() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        tags.put(ContextRepository.TAG_DEPARTMENT_ID, "FOOTWEAR");
//        tags.put(ContextRepository.TAG_STORE_TYPE, "OUTLET");
//        tags.put(ContextRepository.TAG_DEVICE_TYPE, "MOBILE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {   
//            ConfigModel config = new ConfigModel();
//            config.setBrandId("JUSTICE");
//            config.setConfigName("test.config");
//            config.setConfigValue("JUSTICE_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setDeviceType("MOBILE");
//            config.setConfigName("test.config");
//            config.setConfigValue("MOBILE_VALUE");
//            configs.add(config);
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("JUSTICE_VALUE", config.getConfigValue());
//    }    
//    
//    @Test
//    public void testWeakWinner() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        tags.put(ContextRepository.TAG_DEPARTMENT_ID, "FOOTWEAR");
//        tags.put(ContextRepository.TAG_STORE_TYPE, "OUTLET");
//        tags.put(ContextRepository.TAG_DEVICE_TYPE, "MOBILE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {   
//            ConfigModel config = new ConfigModel();
//            config.setConfigName("test.config");
//            config.setConfigValue("JUSTICE_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setDeviceType("MOBILE");
//            config.setConfigName("test.config");
//            config.setConfigValue("MOBILE_VALUE");
//            configs.add(config);
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNotNull(config);
//        assertEquals("MOBILE_VALUE", config.getConfigValue());
//    }
//    
//    @Test
//    public void testNoMatch() {
//        ContextRepository contextRepo = new ContextRepository();
//        
//        Map<String, String> tags = new HashMap<>();
//        tags.put(ContextRepository.TAG_BRAND_ID, "JUSTICE");
//        tags.put(ContextRepository.TAG_DEPARTMENT_ID, "FOOTWEAR");
//        tags.put(ContextRepository.TAG_STORE_TYPE, "OUTLET");
//        tags.put(ContextRepository.TAG_DEVICE_TYPE, "MOBILE");
//        
//        List<ConfigModel> configs = new ArrayList<>();
//        {   
//            ConfigModel config = new ConfigModel();
//            config.setStoreType("REGULAR");
//            config.setConfigName("test.config");
//            config.setConfigValue("JUSTICE_VALUE");
//            configs.add(config);
//        }
//        {            
//            ConfigModel config = new ConfigModel();
//            config.setDeviceType("CASHWRAP");
//            config.seqtConfigName("test.config");
//            config.setConfigValue("CASHWRAP_VALUE");
//            configs.add(config);
//        }
//        
//        ConfigModel config = contextRepo.findMostSpecificConfig(tags, configs);
//        assertNull(config);
//    }    
    
}
