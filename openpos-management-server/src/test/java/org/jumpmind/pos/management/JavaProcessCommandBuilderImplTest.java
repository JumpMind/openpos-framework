package org.jumpmind.pos.management;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.jumpmind.pos.management.OpenposManagementServerConfig.DeviceProcessConfig;
import org.jumpmind.pos.management.OpenposManagementServerConfig.JavaExecutableConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.SocketUtils;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "localtest")
@EnableScheduling
public class JavaProcessCommandBuilderImplTest {
    @Autowired
    JavaProcessCommandBuilderImpl javaBuilder;
    
    static String[] classpathEntriesFromConfig;
    
    DeviceProcessConfig deviceProcessConfig;
    JavaExecutableConfig javaProcessConfig;
    
    @Before
    public void setUp() throws Exception {
        deviceProcessConfig = javaBuilder.config.getDeviceProcessConfig("any");
        javaProcessConfig = deviceProcessConfig.getJavaExecutableConfig();
        if (classpathEntriesFromConfig == null) {
            classpathEntriesFromConfig = javaProcessConfig.getClasspathEntries();
        }
        javaProcessConfig.setProcessPort(OpenposManagementServerConfig.DeviceProcessConfig.AUTO_PORT_ALLOCATION);
        javaProcessConfig.setJavaRemoteDebugPort(null);
        javaProcessConfig.setJavaRemoteDebugArgTemplate(OpenposManagementServerConfig.JavaExecutableConfig.DEFAULT_JAVA_REMOTE_DEBUG_ARG_TEMPLATE);
        javaProcessConfig.setProcessPortArgTemplate(OpenposManagementServerConfig.JavaExecutableConfig.DEFAULT_PROCESS_PORT_ARG_TEMPLATE);
    }

    @Test
    public void testResolveJavaExecutablePath() {
        assertThat(javaBuilder.resolveJavaExecutablePath(javaProcessConfig)).isNotNull().contains(File.separator + "java");
    }

    @Test
    public void testClasspathEntriesFromConfig() {
        javaProcessConfig.setClasspathEntries(classpathEntriesFromConfig);
        assertThat(javaProcessConfig.getClasspathEntries()).hasSize(1);
        String cp = javaBuilder.constructClasspath(javaProcessConfig);
        assertThat(cp).isEqualTo(".");
    }
    @Test
    public void testEmptyClasspath() {
        javaProcessConfig.setClasspathEntries(new String[0]);
        assertThat(javaBuilder.constructClasspath(javaProcessConfig)).isEmpty();
    }
    
    @Test
    public void testMultipleClasspathEntries() {
        javaProcessConfig.setClasspathEntries(new String[]{"c:\\Program Files\\app\\jars", "entry 2", "/users/joe/abc def/g.jar"});
        String cp = javaBuilder.constructClasspath(javaProcessConfig);
        assertThat(cp).isEqualTo("c:\\Program Files\\app\\jars" + File.pathSeparator + "entry 2" + File.pathSeparator + "/users/joe/abc def/g.jar");
    }

    @Test
    public void testAllocatePortUsingAUTO() {
        javaProcessConfig.setProcessPort("AUTO");
        
        Integer allocatedPort = javaBuilder.allocateProcessPort(javaProcessConfig);
        assertThat(allocatedPort).isNotNull();
    }
    
    @Test
    public void testAllocatePortUsingInteger() {
        int availablePort = SocketUtils.findAvailableTcpPort();
        javaProcessConfig.setProcessPort(availablePort + "");
        
        Integer allocatedPort = javaBuilder.allocateProcessPort(javaProcessConfig);
        assertThat(allocatedPort).isEqualTo(availablePort);
    }
    
    @Test
    public void testAllocatePortUsingRange() {
        javaProcessConfig.setProcessPort("5000-5500");
        Integer allocatedPort = javaBuilder.allocateProcessPort(javaProcessConfig);
        assertThat(allocatedPort).isNotNull().isGreaterThanOrEqualTo(5000).isLessThanOrEqualTo(5500);
    }

    @Test
    public void testAllocatePortUsingCommaSepIntegerList() {
        String[] portArray = {" 5000","5500","6000","6500 ","7000", " 7500"};
        List<String> portList = Arrays.asList(portArray);
        List<Integer> portIntList = portList.stream().map(p -> new Integer(p.trim())).collect(Collectors.toList());
        
        javaProcessConfig.setProcessPort(String.join(",", portList));
        Integer allocatedPort = javaBuilder.allocateProcessPort(javaProcessConfig);
        assertThat(allocatedPort).isIn(portIntList);
    }

    @Test
    public void testAllocatePortUsingMultipleRanges() {
        String[] portRangeArray = {" 5000 - 5001 ","6000-6002 ","7000 - 7003"};
        List<String> portList = Arrays.asList(portRangeArray);
        
        javaProcessConfig.setProcessPort(String.join(",", portList));
        Integer allocatedPort = javaBuilder.allocateProcessPort(javaProcessConfig);
        assertThat(allocatedPort).isIn(5000,5001,6000,6001,6002,7000,7001,7002,7003);
    }

    @Test
    public void testAllocatePortUsingMixOfRangesAndInts() {
        String[] portRangeArray = {" 5000-5001","6000", "6001-6002 ","7000 - 7002", "7003"};
        List<String> portList = Arrays.asList(portRangeArray);
        
        javaProcessConfig.setProcessPort(String.join(",", portList));
        Integer allocatedPort = javaBuilder.allocateProcessPort(javaProcessConfig);
        assertThat(allocatedPort).isIn(5000,5001,6000,6001,6002,7000,7001,7002,7003);
    }

    @Test(expected = DeviceProcessConfigException.class)
    public void testAllocatePortUsingMalformedInteger() {
        javaProcessConfig.setProcessPort("a5000");
        Integer allocatedPort = javaBuilder.allocateProcessPort(javaProcessConfig);
        fail("Port should not be allocated and exception should have been raised");
    }
    
    @Test
    public void testProcessRemoteDebugPort_CommandLine() {
        javaProcessConfig.setJavaRemoteDebugPort("6000-7000");
        
        final String remoteDebugArgPatternStr = javaProcessConfig.getJavaRemoteDebugArgTemplate().replace("%d", "([6,7]\\d{3})");
        List<String> commandParts = javaBuilder.constructProcessCommandParts(new DeviceProcessInfo("00000-000", DeviceProcessStatus.NotRunning));
        
        Pattern pattern = Pattern.compile(remoteDebugArgPatternStr);
        Integer debugPort = null;
        for (String part: commandParts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.find()) {
                debugPort = new Integer(matcher.group(1));
            }
        }
        assertThat(debugPort).isBetween(6000, 7000);
    }

    @Test
    public void testProcessProcessPort_CommandLine() {
        javaProcessConfig.setProcessPort("6000-7000");
        
        final String processPortPatternStr = javaProcessConfig.getProcessPortArgTemplate().replace("%d", "([6,7]\\d{3})");
        List<String> commandParts = javaBuilder.constructProcessCommandParts(new DeviceProcessInfo("00000-000", DeviceProcessStatus.NotRunning));
        
        Pattern pattern = Pattern.compile(processPortPatternStr);
        Integer debugPort = null;
        for (String part: commandParts) {
            Matcher matcher = pattern.matcher(part);
            if (matcher.find()) {
                debugPort = new Integer(matcher.group(1));
            }
        }
        assertThat(debugPort).isBetween(6000, 7000);
    }

    @Test
    public void testProcessExecutableJar_CommandLine() {
        javaProcessConfig.setExecutableJarPath("some.jar");
        List<String> commandParts = javaBuilder.constructProcessCommandParts(new DeviceProcessInfo("00000-000", DeviceProcessStatus.NotRunning));
        assertThat(commandParts).endsWith("-jar", "some.jar");
    }

    @Test
    public void testProcessMainClass_CommandLine() {
        javaProcessConfig.setMainClass("org.company.App");
        List<String> commandParts = javaBuilder.constructProcessCommandParts(new DeviceProcessInfo("00000-000", DeviceProcessStatus.NotRunning));
        assertThat(commandParts).endsWith("org.company.App");
    }
    
}
