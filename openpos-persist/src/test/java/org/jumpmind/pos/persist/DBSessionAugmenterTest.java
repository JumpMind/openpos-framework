package org.jumpmind.pos.persist;

import org.jumpmind.pos.persist.cars.AugmentedCarModel;
import org.jumpmind.pos.persist.cars.CarModel;
import org.jumpmind.pos.persist.cars.TestPersistCarsConfig;
import org.jumpmind.pos.persist.model.SearchCriteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestPersistCarsConfig.class})
public class DBSessionAugmenterTest {

    @Autowired
    private DBSessionFactory sessionFactory;

    final String VIN = "KMHCN46C58U242743";

    @Before
    public void setup() {
        {
            DBSession db = sessionFactory.createDbSession();
            db.executeSql("TRUNCATE TABLE CAR_CAR");
        }

        {
            DBSession db = sessionFactory.createDbSession();
            AugmentedCarModel someHyundai = new AugmentedCarModel();
            someHyundai.setVin(VIN);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            someHyundai.setAugmentValue("color", "blue");
            db.save(someHyundai);
        }
    }

    @Test
    public void testQueryByIdHasAugmentedValues() {
        DBSession db = sessionFactory.createDbSession();
        AugmentedCarModel hyundaiLookupedUp = db.findByNaturalId(AugmentedCarModel.class, VIN);
        assertNotNull(hyundaiLookupedUp);
        assertEquals(VIN, hyundaiLookupedUp.getVin());
        assertEquals("Hyundai", hyundaiLookupedUp.getMake());
        assertEquals("Accent", hyundaiLookupedUp.getModel());
        assertEquals("2005", hyundaiLookupedUp.getModelYear());
        assertEquals("blue", hyundaiLookupedUp.getAugmentValue("color"));
        assertEquals("standard", hyundaiLookupedUp.getAugmentValue("transmission"));
    }

    @Test
    public void testQueryByIdSaveAndRequery() {
        DBSession db = sessionFactory.createDbSession();
        AugmentedCarModel hyundaiLookedUp = db.findByNaturalId(AugmentedCarModel.class, VIN);
        assertNotNull(hyundaiLookedUp);
        assertEquals("blue", hyundaiLookedUp.getAugmentValue("color"));
        hyundaiLookedUp.setAugmentValue("color", "green");
        db.save(hyundaiLookedUp);
        AugmentedCarModel relookupModel = db.findByNaturalId(AugmentedCarModel.class, VIN);
        assertNotNull(relookupModel);
        assertEquals("green", relookupModel.getAugmentValue("color"));
    }

    @Test
    public void testQueryBySearchCriteriaWithAugmentedFields() {
        DBSession db = sessionFactory.createDbSession();
        SearchCriteria searchCriteria = new SearchCriteria(AugmentedCarModel.class);
        searchCriteria.addCriteria("color", "blue");
        List<CarModel> cars = db.findByCriteria(searchCriteria);
        assertEquals(1, cars.size());
    }

    @Test
    public void testQueryBySearchCriteriaWithAugmentedFieldsNonFieldsMatch() {
        DBSession db = sessionFactory.createDbSession();
        SearchCriteria searchCriteria = new SearchCriteria(AugmentedCarModel.class);
        searchCriteria.addCriteria("color", "green");
        List<CarModel> cars = db.findByCriteria(searchCriteria);
        assertEquals(0, cars.size());
    }
}
