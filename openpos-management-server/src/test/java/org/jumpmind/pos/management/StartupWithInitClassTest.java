package org.jumpmind.pos.management;

import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class StartupWithInitClassTest {
    @BeforeClass
    public static void init() {
        System.setProperty("spring.profiles.active", "localtest");
        System.setProperty("server.port", SocketUtils.findAvailableTcpPort() + "");
    }

    @After
    public void afterEach() {
        System.setProperty(OpenposManagementServer.STARTUP_INIT_CLASS_PROPERTY, "");
    }
    
    @Test
    public void ensureStartupTaskExecutes() throws Exception {
        System.setProperty(OpenposManagementServer.STARTUP_INIT_CLASS_PROPERTY, "org.jumpmind.pos.management.TestStartupTask");
        OpenposManagementServer.main(new String[] {});
        assertThat(TestStartupTask.invoked).isTrue();
    }

    @Test(expected = OpenposManagementException.class)
    public void ensureStartupFailsWithNonExistentClass() throws Exception {
        System.setProperty(OpenposManagementServer.STARTUP_INIT_CLASS_PROPERTY, "org.NonExistentTask");
        OpenposManagementServer.main(new String[] {});
    }
    
}