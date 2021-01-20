package org.jumpmind.pos.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.jumpmind.pos.persist.DBSession;
import org.jumpmind.pos.service.model.ModuleModel;
import org.jumpmind.pos.test.model.TestTableModel;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {TestConfig.class})
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@ActiveProfiles("test")
public class TestModuleTest {
    
    @Autowired
    TestModule module;
    
    @Autowired
    DBSession testSession;
    
    @Value("${openpos.installationId:undefined}")
    String installationId;
    
    @Before
    public void initModule() {
        module.initialize();
    }
    
    
    @Test
    public void test01ModuleInfoInserted() {
        assertNotNull(module);
        ModuleModel info = testSession.findByNaturalId(ModuleModel.class, installationId);
        assertNotNull(info);
        assertEquals("0.0.1", info.getCurrentVersion());
    }
    
    @Test
    public void test03SqlScript002WasRun() {
        module.setDynamicVersion("0.0.2");
        module.updateDataModel(testSession);
        List<TestTableModel> rows = testSession.findAll(TestTableModel.class, 1000);
        assertNotNull(rows);
        assertEquals(4, rows.size());
    }


}
