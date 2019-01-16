package org.jumpmind.pos.persist;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.cars.CarModel;
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
                "select c0.vin, c0.model_year, c0.make, c0.model, c0.estimated_value, c0.iso_currency_code, c0.create_time, c0.create_by, c0.last_update_time, c0.last_update_by, c0.color, c1.turbo_charged from CAR_CAR c0 join CAR_RACE_CAR c1 on c0.vin=c1.vin where c0.model=${model} and c1.turbo_charged=${turboCharged}",
                sql);
    }
    
    @Test
    public void testGetSelectSqlForEntityNoParams() {
        DBSession db = sessionFactory.createDbSession();
        String sql = db.getSelectSql(CarModel.class, null);
        assertEquals(
                "select c0.vin, c0.model_year, c0.make, c0.model, c0.estimated_value, c0.iso_currency_code, c0.create_time, c0.create_by, c0.last_update_time, c0.last_update_by, c0.color from CAR_CAR c0",
                sql);
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
        }
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN2);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Elantra");
            someHyundai.setModelYear("2005");
            db.save(someHyundai);
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
        }

        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN1);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN1, hyundaiLookupedUp.getVin());
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2006", hyundaiLookupedUp.getModelYear());
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
}
