package org.jumpmind.pos.persist;

import org.joda.money.Money;
import org.jumpmind.db.model.Column;
import org.jumpmind.db.model.Table;
import org.jumpmind.pos.persist.cars.*;
import org.jumpmind.pos.persist.impl.ModelClassMetaData;
import org.jumpmind.pos.persist.impl.ModelValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        assertEquals(nonPKColumns.length,11);
        assertEquals(nonPKColumns[0].getName(),"EFFECTIVE_END_DATE");
        assertEquals(nonPKColumns[1].getName(),"ISO_CURRENCY_CODE");
        assertEquals(nonPKColumns[2].getName(),"RETAIL_PRICE");
        assertEquals(nonPKColumns[3].getName(),"COST");
        assertEquals(nonPKColumns[4].getName(),"TERM_IN_MONTHS");
        assertEquals(nonPKColumns[5].getName(),"VIN");
        assertEquals(nonPKColumns[6].getName(),"CROSS_REF_FIELD");
    }

    @Test
    public void testDelegatePersist() {
        DBSession db = sessionFactory.createDbSession();
        CarExtendedWarrantyServiceModel warranty = new CarExtendedWarrantyServiceModel();
            warranty.setTermInMonths(10);
            warranty.setVin("VINABC123");
            warranty.setWarrantyId("WARRANTY_1234");
            ServiceDefn serviceDefn = new ServiceDefn();
            serviceDefn.setIsoCurrencyCode("USD");
            serviceDefn.setCost(Money.parse("USD 23.87"));
            serviceDefn.setEffectiveStartDate("20191119");
            serviceDefn.setRetailPrice(Money.parse("USD 7.77"));
            warranty.setServiceDefn(serviceDefn);
            db.save(warranty);
    }

    @Test
    public void testDelegateModelValidation() {
        ModelClassMetaData meta = new ModelClassMetaData();
        meta.setClazz(CarExtendedWarrantyServiceModel.class);
        //ModelValidator.validate(meta);
    }

    @Test
    public void testDelegateFindAll() {
        DBSession db = sessionFactory.createDbSession();
        this.testDelegatePersist();
        List<CarExtendedWarrantyServiceModel> warranties = db.findAll(CarExtendedWarrantyServiceModel.class,10);
        assertEquals(warranties.size(),1);
        assertEquals(warranties.get(0).getVin(),"VINABC123");
        assertEquals(warranties.get(0).getWarrantyId(),"WARRANTY_1234");
        assertEquals(warranties.get(0).getIsoCurrencyCode(),"USD");
        assertEquals(warranties.get(0).getCost(),Money.parse("USD 23.87"));
        assertEquals(warranties.get(0).getEffectiveStartDate(),"20191119");
        assertEquals(warranties.get(0).getRetailPrice(),Money.parse("USD 7.77"));
    }

    @Test
    public void testDelagateFindByFields() {
        DBSession db = sessionFactory.createDbSession();
        this.testDelegatePersist();
        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("vin","VINABC123");
        List<CarExtendedWarrantyServiceModel> warranties = db.findByFields(CarExtendedWarrantyServiceModel.class,parms,10);
        assertEquals(warranties.size(),1);
        assertEquals(warranties.get(0).getVin(),"VINABC123");
        assertEquals(warranties.get(0).getWarrantyId(),"WARRANTY_1234");
        assertEquals(warranties.get(0).getIsoCurrencyCode(),"USD");
        assertEquals(warranties.get(0).getCost(),Money.parse("USD 23.87"));
        assertEquals(warranties.get(0).getEffectiveStartDate(),"20191119");
        assertEquals(warranties.get(0).getRetailPrice(),Money.parse("USD 7.77"));
    }

    @Test
    public void testDelagateFindByNaturalId() {
        DBSession db = sessionFactory.createDbSession();
        this.testDelegatePersist();
        Map<String, Object> parms = new HashMap<String, Object>();
        parms.put("vin","VINABC123");
        List<CarExtendedWarrantyServiceModel> warranties = db.findByFields(CarExtendedWarrantyServiceModel.class,parms,10);
        assertEquals(warranties.size(),1);
        assertEquals(warranties.get(0).getVin(),"VINABC123");
        assertEquals(warranties.get(0).getWarrantyId(),"WARRANTY_1234");
        assertEquals(warranties.get(0).getIsoCurrencyCode(),"USD");
        assertEquals(warranties.get(0).getCost(),Money.parse("USD 23.87"));
        assertEquals(warranties.get(0).getEffectiveStartDate(),"20191119");
        assertEquals(warranties.get(0).getRetailPrice(),Money.parse("USD 7.77"));
    }
}