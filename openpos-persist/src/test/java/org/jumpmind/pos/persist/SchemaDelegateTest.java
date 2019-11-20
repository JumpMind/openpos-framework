package org.jumpmind.pos.persist;

import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.pos.persist.cars.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {DelegateTestContextConfig.class})
public class SchemaDelegateTest {

    @Autowired
    private DBSessionFactory sessionFactory;
    private DBSession db;

    @Before
    public void setup() {
        sessionFactory.createAndUpgrade();
    }

    @Test
    public void testDelegateCreation() {
        Table table = sessionFactory.getTables().get(0);
        assertEquals(table.getName(),"CAR_EXTENDED_WARRANTY_SERVICE");
        Column[] pkColumns = table.getPrimaryKeyColumns();
        assertEquals(pkColumns.length,2);
        assertEquals(pkColumns[0].getName(),"WARRANTY_ID");
        assertEquals(pkColumns[1].getName(),"EFFECTIVE_START_DATE");
        Column[] nonPKColumns = table.getNonPrimaryKeyColumns();
        assertEquals(nonPKColumns.length,9);
        assertEquals(nonPKColumns[0].getName(),"TERM_IN_MONTHS");
        assertEquals(nonPKColumns[1].getName(),"EFFECTIVE_END_DATE");
        assertEquals(nonPKColumns[2].getName(),"RETAIL_PRICE");
        assertEquals(nonPKColumns[3].getName(),"COST");
        assertEquals(nonPKColumns[4].getName(),"VIN");

    }

    @Test
    public void testDelegatePersist() {
        DBSession db = sessionFactory.createDbSession();
        CarExtendedWarrantyServiceModel warranty = new CarExtendedWarrantyServiceModel();
            warranty.setTermInMonths(10);
            warranty.setVin("VINABC123");
            warranty.setWarrantyId("WARRANTY_1234");
            ServiceDefn serviceDefn = new ServiceDefn();
            serviceDefn.setCost(new BigDecimal(1.23));
            serviceDefn.setEffectiveStartDate("20191119");
            serviceDefn.setRetailPrice(new BigDecimal(7.77));
            warranty.setServiceDefn(serviceDefn);
            db.save(warranty);
    }

    @Test
    public void testDelegateSelect() {
        DBSession db = sessionFactory.createDbSession();
        this.testDelegatePersist();
        List<CarExtendedWarrantyServiceModel> warranties = db.findAll(CarExtendedWarrantyServiceModel.class,10);
        assertEquals(warranties.size(),1);
    }
}