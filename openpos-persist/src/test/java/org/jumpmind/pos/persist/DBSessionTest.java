package org.jumpmind.pos.persist;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.jumpmind.pos.persist.cars.CarModel;
import org.jumpmind.pos.persist.cars.CarTrimTypeCode;
import org.jumpmind.pos.persist.cars.RaceCarModel;
import org.jumpmind.pos.persist.cars.TestPersistCarsConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestPersistCarsConfig.class })
public class DBSessionTest {

    @Autowired
    private DBSessionFactory sessionFactory;

    @Before
    public void setup() {
        // force reread of metadata
        sessionFactory.createAndUpgrade();
    }
    
    @After
    public void tearDown() {  
    }

    @Test
    public void testGetSelectSqlForEntityWithSuperTableDef() {
        DBSession db = sessionFactory.createDbSession();
        Map<String, Object> params = new HashMap<>();
        params.put("turboCharged", true);
        params.put("model", "Toyota");
        String sql = db.getSelectSql(RaceCarModel.class, params);
        assertEquals(
                "select c0.vin, c0.model_year, c0.make, c0.model, c0.estimated_value, c0.iso_currency_code, c0.car_trim_type_code, c0.image, c0.create_time, c0.create_by, c0.last_update_time, c0.last_update_by, c0.tag_dealership_number, c0.color, c1.turbo_charged from car_car c0 join car_race_car c1 on c0.vin=c1.vin and c0.tag_dealership_number=c1.tag_dealership_number where c0.model=${model} and c1.turbo_charged=${turbocharged}",
                sql.toLowerCase());
    }
    
    @Test
    public void testGetSelectSqlForEntityNoParams() {
        DBSession db = sessionFactory.createDbSession();
        String sql = db.getSelectSql(CarModel.class, null);
        assertEquals(
                "select c0.vin, c0.model_year, c0.make, c0.model, c0.estimated_value, c0.iso_currency_code, c0.car_trim_type_code, c0.image, c0.create_time, c0.create_by, c0.last_update_time, c0.last_update_by, c0.tag_dealership_number, c0.color from car_car c0",
                sql.toLowerCase());
    }

    @Test
    public void testBasicCrud() {
        final String VIN1 = "KMHCN46C58U242743";
        final String VIN2 = "KMHCN46C58U2427432342";
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN1);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
            db.close();
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN2);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Elantra");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
            db.close();
        }

        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
            hyundaiLookupedUp.setModelYear("2006");
            db.save(hyundaiLookupedUp);
            db.close();
        }

        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2006", hyundaiLookupedUp.getModelYear());
            db.close();
        }
    }
    
    @Test
    public void testBlob() throws IOException {
        final String VIN1 = "KMHCN46C58U242743";
        final byte[] imageBytes = IOUtils.resourceToByteArray("/elantra.jpg");
        assertNotNull(imageBytes);
        assertEquals(33340, imageBytes.length);
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN1);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Elantra");
            someHyundai.setModelYear("2012");
            someHyundai.setImage(imageBytes);
            db.save(someHyundai);
            db.close();
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookedUp);
            assertArrayEquals(imageBytes, hyundaiLookedUp.getImage());
        }
        
        
    }
    
    @Test
    public void testMoney() {
        final String VIN1 = "KMHCN46C58U242743";
        final String VIN2 = "KMHCN46C58U2427432342";
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN1);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            someHyundai.setEstimatedValue(Money.of(CurrencyUnit.USD, new BigDecimal("400.00")));
            db.save(someHyundai);
            db.close();
        }        
 
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
            assertEquals("USD", hyundaiLookupedUp.getIsoCurrencyCode());
            assertEquals(new BigDecimal("400.00"), hyundaiLookupedUp.getEstimatedValue().getAmount());
        }
        
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN2);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            someHyundai.setEstimatedValue(Money.of(CurrencyUnit.CAD, new BigDecimal("465.59")));
            db.save(someHyundai);
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN2);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN2, hyundaiLookupedUp.getVin());
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
            assertEquals("CAD", hyundaiLookupedUp.getIsoCurrencyCode());
            assertEquals(new BigDecimal("465.59"), hyundaiLookupedUp.getEstimatedValue().getAmount());
        }        
    }
    
    @Test(expected=PersistException.class)
    public void testMoneyMismatchCurrency() {
        final String VIN1 = "KMHCN46C58U242743";
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN1);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            someHyundai.setIsoCurrencyCode("CAD");
            someHyundai.setEstimatedValue(Money.of(CurrencyUnit.USD, new BigDecimal("400.00")));
            db.save(someHyundai);
            fail("Should not reach here, model has mix of CAD and USD currency.");
        }        
    }
    
    @Test
    public void testModelInheritance() {
        final String VIN1 = "KMHCN46C58U242743";
        {
            DBSession db = sessionFactory.createDbSession();
            RaceCarModel hyundai = new RaceCarModel();
            hyundai.setVin(VIN1);
            hyundai.setMake("Hyundai");
            hyundai.setModel("Tiburon");
            hyundai.setModelYear("2005");
            hyundai.setTurboCharged(true);
            hyundai.setEstimatedValue(Money.of(CurrencyUnit.USD, new BigDecimal("988.34")));
            db.save(hyundai);
        }
        {
            DBSession db = sessionFactory.createDbSession();
            RaceCarModel hyundaiLookupedUp = db.findByNaturalId(RaceCarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Tiburon", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());
            assertEquals("USD", hyundaiLookupedUp.getIsoCurrencyCode());
            assertEquals(new BigDecimal("988.34"), hyundaiLookupedUp.getEstimatedValue().getAmount());
            assertEquals(true, hyundaiLookupedUp.isTurboCharged());
        }         
    }
    
    @Test
    public void testSaveTaggedDefault() {
        final String VIN1 = "KMHCN46C58U242743_TAGGED_DEFAULT";
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundai = new CarModel();
            hyundai.setVin(VIN1);
            hyundai.setMake("Hyundai");
            hyundai.setModel("Accent");
            hyundai.setModelYear("2005");
            hyundai.setEstimatedValue(Money.of(CurrencyUnit.USD, new BigDecimal("988.34")));
            db.save(hyundai);
            db.close();
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("*", hyundaiLookupedUp.getTagValue("DEALERSHIP_NUMBER"));
        }
    }
    @Test
    public void testSaveTagged() {
        final String VIN1 = "KMHCN46C58U242743_TAGGED";
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundai = new CarModel();
            hyundai.setVin(VIN1);
            hyundai.setMake("Hyundai");
            hyundai.setModel("Accent");
            hyundai.setModelYear("2005");
            hyundai.setEstimatedValue(Money.of(CurrencyUnit.USD, new BigDecimal("988.34")));
            hyundai.setTagValue("DEALERSHIP_NUMBER", "DLRSHIP1234");
            db.save(hyundai);
            db.close();
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("DLRSHIP1234", hyundaiLookupedUp.getTagValue("DEALERSHIP_NUMBER"));
        }
    }
    
    @Test
    public void testSaveTaggedSubclass() {
        final String VIN1 = "KMHCN46C58U242743_TAGGED_SUBCLASS";
        {
            DBSession db = sessionFactory.createDbSession();
            RaceCarModel hyundai = new RaceCarModel();
            hyundai.setVin(VIN1);
            hyundai.setMake("Hyundai");
            hyundai.setModel("Tiburon");
            hyundai.setModelYear("2005");
            hyundai.setTurboCharged(true);
            hyundai.setEstimatedValue(Money.of(CurrencyUnit.USD, new BigDecimal("988.34")));
            hyundai.setTagValue("DEALERSHIP_NUMBER", "DLRSHIP1234");
            db.save(hyundai);
            db.close();
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("DLRSHIP1234", hyundaiLookupedUp.getTagValue("DEALERSHIP_NUMBER"));
        }
    }
    
    @Test
    public void testTypeCodeCrud() {
        final String VIN1 = "KMHCN46C58U242743";
        final String VIN2 = "KMHCN46C58U2427432342";
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN1);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            someHyundai.setCarTrimTypeCode(CarTrimTypeCode.EX);
            db.save(someHyundai);
            db.close();
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN2);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Elantra");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
            db.close();
        }

        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals(CarTrimTypeCode.EX, hyundaiLookupedUp.getCarTrimTypeCode());
            hyundaiLookupedUp.setCarTrimTypeCode(CarTrimTypeCode.LX);
            db.save(hyundaiLookupedUp);
            db.close();
        }

        CarTrimTypeCode NEW_EX_CODE = CarTrimTypeCode.of("NEW_EX");
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals(CarTrimTypeCode.LX, hyundaiLookupedUp.getCarTrimTypeCode());
            hyundaiLookupedUp.setCarTrimTypeCode(NEW_EX_CODE);
            db.save(hyundaiLookupedUp);
            db.close();
        }
        
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals(CarTrimTypeCode.of("NEW_EX"), hyundaiLookupedUp.getCarTrimTypeCode());
            assertEquals(NEW_EX_CODE, hyundaiLookupedUp.getCarTrimTypeCode());
            
            db.close();
        }
    }
    
    @Test
    public void testFindByFields() {
        final String VIN1 = "KMHCN46C58U242743";
        final String VIN2 = "KMHCN46C58U2427432342";
        
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN1);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
            db.close();
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN2);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Elantra");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
            db.close();
        }        
        
        Map<String, Object> fieldValues = new HashMap<>();
        fieldValues.put("vin", VIN1);
        
        {
            DBSession db = sessionFactory.createDbSession();
            List<CarModel> cars = db.findByFields(CarModel.class, fieldValues);
            assertNotNull(cars);
            assertEquals(1, cars.size());
            assertEquals(VIN1, cars.get(0).getVin());
            
            db.close();
        }
    }
    
    
}
