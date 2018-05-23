package org.jumpmind.pos.service.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.persist.cars.TestConfig;
import org.jumpmind.pos.service.model.ModuleInfo;
import org.jumpmind.pos.service.test.model.TestTable;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestModuleTest {
    
    @Autowired
    TestModule module;
    
    @Autowired
    DBSession userSession;
    
    @Value("${installation.id}")
    String installationId;
    
    @Test
    public void test01ModuleInfoInserted() {
        assertNotNull(module);
        ModuleInfo info = userSession.findByNaturalId(ModuleInfo.class, installationId);
        assertNotNull(info);
        assertEquals("0.0.1", info.getCurrentVersion());
    }
    
    @Test
    public void test02SqlScript001WasRun() {
        List<TestTable> rows = userSession.findAll(TestTable.class);
        assertNotNull(rows);
        assertEquals(2, rows.size());
    }
    
    @Test
    public void test03SqlScript002WasRun() {
        module.setDynamicVersion("0.0.2");
        module.updateDataModel(userSession);
        List<TestTable> rows = userSession.findAll(TestTable.class);
        assertNotNull(rows);
        assertEquals(4, rows.size());
    }


}
