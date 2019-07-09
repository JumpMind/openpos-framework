package org.jumpmind.pos.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.PersistException;
import org.jumpmind.pos.persist.Query;
import org.jumpmind.pos.persist.cars.CarModel;
import org.jumpmind.pos.persist.cars.CarStats;
import org.jumpmind.pos.persist.cars.RaceCarModel;
import org.jumpmind.pos.persist.cars.TestPersistCarsConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestPersistCarsConfig.class})
public class DBSessionQueryTest {

    @Autowired
    private DBSessionFactory sessionFactory;

    final static String VIN = "KMHCN46C58U242743";
    
    
    @Before
    public void setup() {
        {            
            DBSession db = sessionFactory.createDbSession();
            db.executeSql("TRUNCATE TABLE CAR_CAR");
        }


        {            
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
        }
        {            
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN + "2342");
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Elantra");
            someHyundai.setModelYear("2006");
            db.save(someHyundai);
        }             
        {            
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
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

        Query<CarModel> byMakeAndModel = new Query<CarModel>()
                .named("byMakeAndModel")
                .result(CarModel.class);

        List<CarModel> hyundais = db.query(byMakeAndModel, "Hyundai", 100);
        assertEquals(3, hyundais.size());

        {
            CarModel hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }

        {            
            CarModel hyundaiLookupedUp = hyundais.get(1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Elantra", hyundaiLookupedUp.getModel());
            assertEquals("2006", hyundaiLookupedUp.getModelYear());            
        }
    }
    
    @Test
    public void testMultiParamQuery() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarModel> byMakeAndModel = new Query<CarModel>()
                .named("byMakeAndModel")
                .result(CarModel.class);
        
        Map<String, Object> params = new HashMap<>();
        params.put("make", "Hyundai");
        params.put("model", "Accent");
        
        List<CarModel> hyundais = db.query(byMakeAndModel, params, 100);
        assertEquals(1, hyundais.size());
        
        {
            CarModel hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
    }
    
    
    @Test
    public void testOptionalParamQuery() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarModel> byMakeAndModel = new Query<CarModel>()
                .named("byMakeAndModel")
                .result(CarModel.class);        
        
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("model", "Accent");
            
            List<CarModel> hyundais = db.query(byMakeAndModel, params, 100);
            assertEquals(1, hyundais.size());
            CarModel hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            
            List<CarModel> hyundais = db.query(byMakeAndModel, params, 100);
            assertEquals(3, hyundais.size());
            CarModel hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
    }
    
    @Test
    public void testNoResults() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarModel> byMakeAndModel = new Query<CarModel>()
                .named("byMakeAndModel")
                .result(CarModel.class);
        
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Ford");
            params.put("model", "Falcon");
            
            List<CarModel> falcons = db.query(byMakeAndModel, params, 100);
            assertNotNull(falcons);
            assertEquals(0, falcons.size());
        }
    }        
    
    @Test
    public void testNoWhereClause() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarModel> allCars = new Query<CarModel>()
                .result(CarModel.class);
        
        {
            List<CarModel> cars = db.query(allCars);
            assertEquals(3, cars.size());
        }
    }
    
    @Test
    public void testOnlyOptionalWhere() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarModel> byMakeAndModel = new Query<CarModel>()
                .named("byMakeAndModel")
                .result(CarModel.class);
        
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("model", "Accent");
            
            List<CarModel> hyundais = db.query(byMakeAndModel, params, 100);
            assertEquals(1, hyundais.size());
            CarModel hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            
            List<CarModel> hyundais = db.query(byMakeAndModel, params, 100);
            assertEquals(3, hyundais.size());
            CarModel hyundaiLookupedUp = hyundais.get(0);
            assertNotNull(hyundaiLookupedUp);
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
        }
    }
    
    @Test(expected = PersistException.class)
    public void testMissingParameter() {
        DBSession db = sessionFactory.createDbSession();
        
        Query<CarModel> byMakeAndModel = new Query<CarModel>()
                .named("byMakeAndModel")
                .result(CarModel.class);
        
        Map<String, Object> params = new HashMap<>();
        db.query(byMakeAndModel, params, 100);
    }
    
    @Test
    public void testAdHocQueryOnePartStatement() {
        DBSession db = sessionFactory.createDbSession();
        Query<CarStats> modelCounts = new Query<CarStats>()
                .named("carCountByModel")
                .result(CarStats.class);
        
        List<CarStats> carStats = db.query(modelCounts, "Hyundai", 100);
        
        assertEquals(3, carStats.size());
        
        assertEquals("Accent", carStats.get(0).getModel());
        assertEquals("Elantra", carStats.get(1).getModel());
        assertEquals("Santa Fe", carStats.get(2).getModel());
    }
    
    @Test
    public void testCarSearchYaml() {
        DBSession db = sessionFactory.createDbSession();
        Query<CarModel> carsSearch = new Query<CarModel>()
                .named("carSearch")
                .result(CarModel.class);
        
        {          
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            
            List<CarModel> searchResults = db.query(carsSearch, params, 100);
            assertEquals(3, searchResults.size());
            assertEquals("Accent", searchResults.get(0).getModel());
            assertEquals("Elantra", searchResults.get(1).getModel());
            assertEquals("Santa Fe", searchResults.get(2).getModel());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("model", "Accent");
            
            List<CarModel> searchResults = db.query(carsSearch, params, 100);
            assertEquals(1, searchResults.size());
            assertEquals("Accent", searchResults.get(0).getModel());
        }
        {
            Map<String, Object> params = new HashMap<>();
            params.put("make", "Hyundai");
            params.put("year", "2005");
            
            List<CarModel> searchResults = db.query(carsSearch, params, 100);
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
        
        List<CarStats> carStats = db.query(modelCounts, "Hyundai", 100);
        
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
        
        sessionFactory.createAndUpgrade();
        db.close();
        db = sessionFactory.createDbSession();
        
        Query<CarModel> allCars = new Query<CarModel>()
                .result(CarModel.class);
        
        {
            List<CarModel> cars = db.query(allCars);
            assertEquals(3, cars.size());
            assertNull(cars.get(0).getAdditionalField("color"));
            assertEquals("grey", cars.get(1).getAdditionalField("color"));
            assertEquals("blue", cars.get(2).getAdditionalField("color"));
        }        
    }
    
    @Test 
    public void testLiteralQueryReplacementInSelectClause() {
        sessionFactory.createAndUpgrade();
        DBSession db = sessionFactory.createDbSession();
        db.executeScript(new StringReader("update car_car set color = 'grey' where model = 'Accent';"
                + "update car_car set color = 'blue' where model = 'Elantra';"));
        
        Query<CarModel> carsByLiteralReplacementInSelectClause = new Query<CarModel>()
                .named("carsByLiteralReplacementInSelectClause")
                .result(CarModel.class);
        
        Map<String, Object> params = new HashMap<>();
        params.put("columnName", "color");
        params.put("columnValue", "blue");
        
        List<CarModel> cars = db.query(carsByLiteralReplacementInSelectClause, params, 100);
        assertEquals(1, cars.size());
        assertEquals("Hyundai", cars.get(0).getMake());
        assertEquals("Elantra", cars.get(0).getModel());
        assertEquals("blue", cars.get(0).getAdditionalField("color"));
    }
    @Test 
    public void testLiteralQueryReplacementInWhereClause() {
        sessionFactory.createAndUpgrade();
        DBSession db = sessionFactory.createDbSession();
        db.executeScript(new StringReader("update car_car set color = 'grey' where model = 'Accent';"
                + "update car_car set color = 'blue' where model = 'Elantra';"));
        
        Query<CarModel> carsByLiteralFieldMatch = new Query<CarModel>()
                .named("carsByLiteralReplacementInWhereClause")
                .result(CarModel.class);
        
        Map<String, Object> params = new HashMap<>();
        params.put("columnName", "color");
        params.put("columnValue", "blue");
        
        List<CarModel> cars = db.query(carsByLiteralFieldMatch, params, 100);
        assertEquals(1, cars.size());
        assertEquals("Hyundai", cars.get(0).getMake());
        assertEquals("Elantra", cars.get(0).getModel());
        assertEquals("blue", cars.get(0).getAdditionalField("color"));
    }
    
    @Test 
    public void testLiteralQueryReplacementInOptionalWhereClause() {
        sessionFactory.createAndUpgrade();
        DBSession db = sessionFactory.createDbSession();
        db.executeScript(new StringReader("update car_car set color = 'grey' where model = 'Accent';"
                + "update car_car set color = 'blue' where model = 'Elantra';"));
        
        Query<CarModel> carsByLiteralReplacementInOptionalWhereClause = new Query<CarModel>()
                .named("carsByLiteralReplacementInOptionalWhereClause")
                .result(CarModel.class);
        
        Map<String, Object> params = new HashMap<>();
        params.put("columnName", "color");
        params.put("columnValue", "blue");
        
        List<CarModel> cars = db.query(carsByLiteralReplacementInOptionalWhereClause, params, 100);
        assertEquals(1, cars.size());
        assertEquals("Hyundai", cars.get(0).getMake());
        assertEquals("Elantra", cars.get(0).getModel());
        assertEquals("blue", cars.get(0).getAdditionalField("color"));
    }
    
    
    @Test
    public void testTwoTableSubclassSaveAndQuery() {
        sessionFactory.createAndUpgrade();
        DBSession db = sessionFactory.createDbSession();
        RaceCarModel raceCar = new RaceCarModel();
        raceCar.setVin(VIN + "6140");
        raceCar.setMake("Toyota");
        raceCar.setModel("Tercel");
        raceCar.setModelYear("1995");
        raceCar.setTurboCharged(true);
        db.save(raceCar);
        
        RaceCarModel fromDb = db.findByNaturalId(RaceCarModel.class, raceCar.getVin());
        assertEquals(raceCar.getVin(), fromDb.getVin());
        assertEquals(raceCar.getMake(), fromDb.getMake());
        assertEquals(raceCar.isTurboCharged(), fromDb.isTurboCharged());
        
        CarModel subclass = db.findByNaturalId(CarModel.class, raceCar.getVin());
        assertEquals(raceCar.getVin(), subclass.getVin());
        assertEquals(raceCar.getMake(), subclass.getMake());
    }
}
