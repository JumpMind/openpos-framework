package org.jumpmind.pos.persist;

import org.jumpmind.pos.persist.cars.CarModel;
import org.jumpmind.pos.persist.cars.CarModelExtension;
import org.jumpmind.pos.persist.cars.TestPersistCarsConfig;
import org.jumpmind.pos.util.DefaultObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestPersistCarsConfig.class})
public class DBSessionExtensionModelTest {
    @Autowired
    private DBSessionFactory sessionFactory;

    @Before
    public void setup() {
        {
            DBSession db = sessionFactory.createDbSession();
            db.executeSql("TRUNCATE TABLE CAR_CAR");
            db.executeSql("TRUNCATE TABLE CAR_SERVICE_INVOICE");
        }

        final String VIN = "KMHCN46C58U242743";

        {
            DBSession db = sessionFactory.createDbSession();
            CarModel someHyundai = new CarModel();
            someHyundai.setVin(VIN);
            someHyundai.setMake("Hyundai");
            someHyundai.setModel("Accent");
            someHyundai.setModelYear("2005");

            CarModelExtension ext = new CarModelExtension();
            ext.setTrailerHitch(true);

            someHyundai.addExtension(CarModelExtension.class, ext);

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
            someHyundai.setModel("Sante Fe");
            someHyundai.setModelYear("2007");
            db.save(someHyundai);
        }
    }

    @Test
    public void testSingleNaturalId() {
        final String VIN = "KMHCN46C58U242743";
        {
            DBSession db = sessionFactory.createDbSession();
            CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN);
            CarModelExtension carModelExtension = ((CarModelExtension)hyundaiLookupedUp.getExtension(CarModelExtension.class));

            assertNotNull(carModelExtension);
            assertTrue(carModelExtension.isTrailerHitch());


        }
    }

    @Test
    public void testExtensionSerialization() throws Exception {
        final String VIN = "KMHCN46C58U242743";
        DBSession db = sessionFactory.createDbSession();
        CarModel hyundaiLookupedUp = db.findByNaturalId(CarModel.class, VIN);
        CarModelExtension carModelExtension = ((CarModelExtension)hyundaiLookupedUp.getExtension(CarModelExtension.class));

        assertNotNull(carModelExtension);
        assertTrue(carModelExtension.isTrailerHitch());

        String serializedCarModel = DefaultObjectMapper.defaultObjectMapper().writeValueAsString(hyundaiLookupedUp);
        CarModel carModel = DefaultObjectMapper.defaultObjectMapper().readValue(serializedCarModel, CarModel.class);
        CarModelExtension deserialized = carModel.getExtension(CarModelExtension.class);
        assertTrue(deserialized.isTrailerHitch());
    }

}
