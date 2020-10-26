package org.jumpmind.pos.persist;

import org.jumpmind.db.sql.Row;
import org.jumpmind.pos.persist.cars.MultiAugmentedCarModel;
import org.jumpmind.pos.persist.cars.TestPersistCarsConfig;
import org.jumpmind.pos.persist.model.SearchCriteria;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestPersistCarsConfig.class})
public class DBSessionMultipleAugmenterTest {

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
            MultiAugmentedCarModel someHyundai = new MultiAugmentedCarModel();
            someHyundai.setVin(VIN);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");
            someHyundai.setAugmentValue("color", "blue");
            someHyundai.setAugmentValue("department", "new");
            someHyundai.setAugmentValue("section", "2");
            db.save(someHyundai);
        }
    }

    @Test
    public void testQueryByIdHasAugmentedValues() {
        DBSession db = sessionFactory.createDbSession();
        MultiAugmentedCarModel hyundaiLookupedUp = db.findByNaturalId(MultiAugmentedCarModel.class, VIN);
        assertNotNull(hyundaiLookupedUp);
        assertEquals(VIN, hyundaiLookupedUp.getVin());
        assertEquals("Hyundai", hyundaiLookupedUp.getMake());
        assertEquals("Accent", hyundaiLookupedUp.getModel());
        assertEquals("2005", hyundaiLookupedUp.getModelYear());
        assertEquals("blue", hyundaiLookupedUp.getAugmentValue("color"));
        assertEquals("standard", hyundaiLookupedUp.getAugmentValue("transmission"));
        assertEquals("new", hyundaiLookupedUp.getAugmentValue("department"));
        assertEquals("2", hyundaiLookupedUp.getAugmentValue("section"));
    }

    @Test
    public void testQueryByIdSaveAndRequery() {
        DBSession db = sessionFactory.createDbSession();
        MultiAugmentedCarModel hyundaiLookedUp = db.findByNaturalId(MultiAugmentedCarModel.class, VIN);
        assertNotNull(hyundaiLookedUp);
        assertEquals("blue", hyundaiLookedUp.getAugmentValue("color"));
        hyundaiLookedUp.setAugmentValue("color", "green");
        db.save(hyundaiLookedUp);
        MultiAugmentedCarModel relookupModel = db.findByNaturalId(MultiAugmentedCarModel.class, VIN);
        assertNotNull(relookupModel);
        assertEquals("green", relookupModel.getAugmentValue("color"));
    }

    @Test
    public void testQueryBySearchCriteriaWithAugmentedFields() {
        DBSession db = sessionFactory.createDbSession();
        SearchCriteria searchCriteria = new SearchCriteria(MultiAugmentedCarModel.class);
        searchCriteria.addCriteria("color", "blue");
        List<MultiAugmentedCarModel> cars = db.findByCriteria(searchCriteria);
        assertEquals(1, cars.size());

        searchCriteria = new SearchCriteria(MultiAugmentedCarModel.class);
        searchCriteria.addCriteria("department", "new");
        cars = db.findByCriteria(searchCriteria);
        assertEquals(1, cars.size());
    }

    @Test
    public void testQueryBySearchCriteriaWithAugmentedFieldsNonFieldsMatch() {
        DBSession db = sessionFactory.createDbSession();
        SearchCriteria searchCriteria = new SearchCriteria(MultiAugmentedCarModel.class);
        searchCriteria.addCriteria("color", "green");
        List<MultiAugmentedCarModel> cars = db.findByCriteria(searchCriteria);
        assertEquals(0, cars.size());
    }

    private Row findRowByColumn(List<Row> results, String columnName, String value) {
        return results.stream().filter(row -> Objects.equals(value, row.getString(columnName))).findFirst().orElse(null);
    }
}
