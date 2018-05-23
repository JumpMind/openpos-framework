package org.jumpmind.pos.persist.cars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.InputStream;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.Query;
import org.jumpmind.pos.persist.impl.DatabaseSchema;
import org.jumpmind.pos.persist.impl.QueryTemplates;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestConfig.class})
public class DBSessionQueryTest {

    @Autowired
    private DBSessionFactory sessionFactory;

    @Before
    public void setup() {
        {            
            DBSession db = sessionFactory.createDbSession();
            db.executeSql("TRUNCATE TABLE CAR_CAR");
        }

        final String VIN = "KMHCN46C58U242743";
        String rowId = null;

        {            
            DBSession db = sessionFactory.createDbSession();
            CarEntity someHyundai = new CarEntity();
            someHyundai.setVin(VIN);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
        }
        {            
            DBSession db = sessionFactory.createDbSession();
            CarEntity someHyundai = new CarEntity();
            someHyundai.setVin(VIN + "2342");
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Elantra");
            someHyundai.setModelYear("2006");
            db.save(someHyundai);
        }             
        {            
            DBSession db = sessionFactory.createDbSession();
            CarEntity someHyundai = new CarEntity();
            someHyundai.setVin(VIN + "4554");
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Santa Fe");
            someHyundai.setModelYear("2007");
            db.save(someHyundai);
        }             
    }        

    @Test
    public void testSingleParamQuery() {
        DBSession db = sessionFactory.createDbSession();

        Query<CarEntity> byMakeAndModel = new Query<CarEntity>()
                .named("byMakeAndModel")
                .result(CarEntity.class);

        List<CarEntity> hyundais = db.query(byMakeAndModel, "Hyundai");
        assertEquals(3, hyundais.size());

        {
            CarEntity hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }

        {            
            CarEntity hyundaiLookupedUp = hyundais.get(1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Elantra", hyundaiLookupedUp.getModel());
            assertEquals("2006", hyundaiLookupedUp.getModelYear());            
        }
    }
    
    @Test
    public void testMultiParamQuery() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarEntity> byMakeAndModel = new Query<CarEntity>()
                .named("byMakeAndModel")
                .result(CarEntity.class);
        
        Map<String, Object> params = new HashMap<>();
        params.put("make", "Hyundai");
        params.put("model", "Accent");
        
        List<CarEntity> hyundais = db.query(byMakeAndModel, params);
        assertEquals(1, hyundais.size());
        
        {
            CarEntity hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
    }
    
    
    @Test
    public void testOptionalParamQuery() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarEntity> byMakeAndModel = new Query<CarEntity>()
                .named("byMakeAndModel")
                .result(CarEntity.class);        
        
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("model", "Accent");
            
            List<CarEntity> hyundais = db.query(byMakeAndModel, params);
            assertEquals(1, hyundais.size());
            CarEntity hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            
            List<CarEntity> hyundais = db.query(byMakeAndModel, params);
            assertEquals(3, hyundais.size());
            CarEntity hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
    }
    
    private void result(Class<CarEntity> class1) {
    }

    @Test
    public void testNoResults() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarEntity> byMakeAndModel = new Query<CarEntity>()
                .named("byMakeAndModel")
                .result(CarEntity.class);
        
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Ford");
            params.put("model", "Falcon");
            
            List<CarEntity> falcons = db.query(byMakeAndModel, params);
            assertNull(falcons);
        }
    }        
    
    @Test
    public void testNoWhereClause() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarEntity> allCars = new Query<CarEntity>()
                .result(CarEntity.class);
        
        {
            List<CarEntity> cars = db.query(allCars);
            assertEquals(3, cars.size());
        }
    }
    
    @Test
    public void testOnlyOptionalWhere() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarEntity> byMakeAndModel = new Query<CarEntity>()
                .named("byMakeAndModel")
                .result(CarEntity.class);
        
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("model", "Accent");
            
            List<CarEntity> hyundais = db.query(byMakeAndModel, params);
            assertEquals(1, hyundais.size());
            CarEntity hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            
            List<CarEntity> hyundais = db.query(byMakeAndModel, params);
            assertEquals(3, hyundais.size());
            CarEntity hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
    }
    
    @Test(expected = PersistException.class)
    public void testMissingParameter() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarEntity> byMakeAndModel = new Query<CarEntity>()
                .named("byMakeAndModel")
                .result(CarEntity.class);
        
        Map<String, Object> params = new HashMap<>();
        db.query(byMakeAndModel, params);
    }
    
    @Test
    public void testAdHocQueryOnePartStatement() {
        DBSession db = sessionFactory.createDbSession();
        Query<CarStats> modelCounts = new Query<CarStats>()
                .named("carCountByModel")
                .result(CarStats.class);
        
        List<CarStats> carStats = db.query(modelCounts, "Hyundai");
        
        assertEquals(3, carStats.size());
        
        assertEquals("Accent", carStats.get(0).getModel());
        assertEquals("Elantra", carStats.get(1).getModel());
        assertEquals("Santa Fe", carStats.get(2).getModel());
    }
    
    @Test
    public void testCarSearchYaml() {
        DBSession db = sessionFactory.createDbSession();
        Query<CarEntity> carsSearch = new Query<CarEntity>()
                .named("carSearch")
                .result(CarEntity.class);
        
        {          
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            
            List<CarEntity> searchResults = db.query(carsSearch, params);
            assertEquals(3, searchResults.size());
            assertEquals("Accent", searchResults.get(0).getModel());
            assertEquals("Elantra", searchResults.get(1).getModel());
            assertEquals("Santa Fe", searchResults.get(2).getModel());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("model", "Accent");
            
            List<CarEntity> searchResults = db.query(carsSearch, params);
            assertEquals(1, searchResults.size());
            assertEquals("Accent", searchResults.get(0).getModel());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("year", "2005");
            
            List<CarEntity> searchResults = db.query(carsSearch, params);
            assertEquals(1, searchResults.size());
            assertEquals("Accent", searchResults.get(0).getModel());
        }
        
    }    
    
    @Test
    public void testAdHocQuery() {
        DBSession db = sessionFactory.createDbSession();
        Query<CarStats> modelCounts = new Query<CarStats>()
                .named("carCountByModel_Segmented")
                .result(CarStats.class);
        
        List<CarStats> carStats = db.query(modelCounts, "Hyundai");
        
        assertEquals(3, carStats.size());
        
        assertEquals("Accent", carStats.get(0).getModel());
        assertEquals("Elantra", carStats.get(1).getModel());
        assertEquals("Santa Fe", carStats.get(2).getModel());
    }
    
    @Test
    public void testExtraFields() {
        DBSession db = sessionFactory.createDbSession();
        db.executeScript(new StringReader("update car_car set color = 'grey' where model = 'Accent';"
                + "update car_car set color = 'blue' where model = 'Elantra';"));
        
        sessionFactory.reloadSchema();
        db.close();
        db = sessionFactory.createDbSession();
        
        Query<CarEntity> allCars = new Query<CarEntity>()
                .result(CarEntity.class);
        
        {
            List<CarEntity> cars = db.query(allCars);
            assertEquals(3, cars.size());
            assertNull(cars.get(0).getAdditionalField("color"));
            assertEquals("grey", cars.get(1).getAdditionalField("color"));
            assertEquals("blue", cars.get(2).getAdditionalField("color"));
        }        
    }
}
