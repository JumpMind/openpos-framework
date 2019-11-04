package org.jumpmind.pos.management;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.FileSystemUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "localtest")
public class ProcessManagerEnvironmentServiceTest {

    @Autowired
    ProcessManagerEnvironmentService envSvc;
    
    @Autowired
    OpenposManagementServerConfig config;
    
    @Before
    public void setUp() throws Exception {
        
    }

    @Test
    public void testMainWorkDirCreate() {
        // First remove work dir if it exists
        File workDir = new File(config.getMainWorkDirPath());
        if (workDir.exists()) {
            if (! FileSystemUtils.deleteRecursively(workDir)) {
                fail("Failed to delete work dir: '" + workDir.getPath() + "'" );
            }
        }
        
        assertThat(workDir.getPath()).isEqualTo("./build/test-work");
        envSvc.ensureMainWorkDirExists();
        assertTrue(workDir.exists());
    }
}
