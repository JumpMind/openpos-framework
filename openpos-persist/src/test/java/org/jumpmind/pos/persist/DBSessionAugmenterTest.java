package org.jumpmind.pos.persist;

import org.jumpmind.db.sql.Row;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    @Test
    public void testDefaultValueIsSetWhenCreatingNewItem() {
        DBSession db = sessionFactory.createDbSession();
        AugmentedCarModel car = db.findByNaturalId(AugmentedCarModel.class, VIN);
        assertEquals("standard", car.getAugmentValue("transmission"));
    }

    @Test
    public void testIndexCreationSingleColumnIndex() {
        DBSession db = sessionFactory.createDbSession();
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", "CAR_AUGMENTED_CAR");
        params.put("indexName", "CAR_IDX_OPTION_COLOR");
        List<Row> results = db.query("select * from INFORMATION_SCHEMA.INDEXES where " +
                "TABLE_NAME = :tableName and INDEX_NAME = :indexName", params);
        assertEquals(1, results.size());
        assertEquals("OPTION_COLOR", results.get(0).getString("COLUMN_NAME"));
    }

    @Test
    public void testIndexCreationMultipleColumnIndex() {
        DBSession db = sessionFactory.createDbSession();
        Map<String, Object> params = new HashMap<>();
        params.put("tableName", "CAR_AUGMENTED_CAR");
        params.put("indexName", "CAR_IDX_MAKE_MODEL_COLOR");
        List<Row> results = db.query("select * from INFORMATION_SCHEMA.INDEXES where " +
                "TABLE_NAME = :tableName and INDEX_NAME = :indexName", params);
        assertEquals(3, results.size());
        assertNotNull("Expecting OPTION_COLOR to be indexed", findRowByColumn(results, "COLUMN_NAME", "OPTION_COLOR"));
        assertNotNull("Expecting MAKE to be indexed", findRowByColumn(results, "COLUMN_NAME", "MAKE"));
        assertNotNull("Expecting MODEL to be indexed", findRowByColumn(results, "COLUMN_NAME", "MODEL"));
    }

    private Row findRowByColumn(List<Row> results, String columnName, String value) {
        return results.stream().filter(row -> Objects.equals(value, row.getString(columnName))).findFirst().orElse(null);
    }
}
