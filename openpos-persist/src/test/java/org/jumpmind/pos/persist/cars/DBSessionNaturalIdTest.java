package org.jumpmind.pos.persist.cars;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.DBSessionFactory;
import org.jumpmind.pos.persist.PersistException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestConfig.class})
public class DBSessionNaturalIdTest {

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
            someHyundai.setModel("Sante Fe");
            someHyundai.setModelYear("2007");
            db.save(someHyundai);
        }             
        {            
            DBSession db = sessionFactory.createDbSession();
            ServiceInvoice invoice = new ServiceInvoice();
            invoice.setInvoiceDate(clearTime(new Date()));
            invoice.setInvoiceLocation(10);
            invoice.setInvoiceNumber(500);
            invoice.setInvoiceStatus("OPEN");
            invoice.setInvoiceTotal(new BigDecimal("497.99"));
            db.save(invoice);
        }             
    }     

    
    @Test(expected=PersistException.class)
    public void testNotEnoughCritera() {
        DBSession db = sessionFactory.createDbSession();
        db.findByNaturalId(ServiceInvoice.class, "test");
    }

    @Test
    public void testSingleNaturalId() {
        final String VIN = "KMHCN46C58U242743";
        {            
            DBSession db = sessionFactory.createDbSession();
            CarEntity hyundaiLookupedUp = db.findByNaturalId(CarEntity.class, VIN);
            assertNotNull(hyundaiLookupedUp);
            assertEquals(VIN, hyundaiLookupedUp.getVin());
            assertEquals("Hyundai", hyundaiLookupedUp.getMake());
            assertEquals("Accent", hyundaiLookupedUp.getModel());
            assertEquals("2005", hyundaiLookupedUp.getModelYear());            
        }
    }
    @Test
    public void testCompositeNaturalId() {
        {            
            DBSession db = sessionFactory.createDbSession();
            ServiceInvoiceId id = new ServiceInvoiceId();
            id.setInvoiceDate(clearTime(new Date()));
            id.setInvoiceLocation(10);
            id.setInvoiceNumber(500);
            
            ServiceInvoice invoice = db.findByNaturalId(ServiceInvoice.class, id);
            assertNotNull(invoice);
            
            invoice.setInvoiceDate(clearTime(new Date()));
            invoice.setInvoiceLocation(10);
            invoice.setInvoiceNumber(500);
            invoice.setInvoiceStatus("OPEN");
            invoice.setInvoiceTotal(new BigDecimal("497.99"));
            
            assertEquals(clearTime(new Date()), invoice.getInvoiceDate());
            assertEquals(10, invoice.getInvoiceLocation());
            assertEquals(500, invoice.getInvoiceNumber());
            assertEquals("OPEN", invoice.getInvoiceStatus());
            assertEquals(new BigDecimal("497.99"), invoice.getInvoiceTotal());
        }
    }
    
    static Date clearTime(Date d) {
        return org.apache.commons.lang3.time.DateUtils.truncate(new Date(), Calendar.DATE);
    }
    
}