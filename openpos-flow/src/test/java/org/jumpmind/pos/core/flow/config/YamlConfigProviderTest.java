package org.jumpmind.pos.core.flow.config;

import org.jumpmind.pos.core.flow.TestStates;
import org.junit.Test;

import static org.junit.Assert.*;

public class YamlConfigProviderTest {
    
    @Test
    public void testLoadResources() {
        YamlConfigProvider provider = new YamlConfigProvider();
        provider.load("pos", "testflows");
        
        provider.load("selfcheckout", "testflows");
        provider.load("selfcheckout", "testflows/selfcheckout");
        
    }

    @Test
    public void testExtendFlow() {
        YamlConfigProvider provider = new YamlConfigProvider();
        provider.load("pos", "testflows");

        FlowConfig config = provider.getConfigByName("pos", "11111", "TestFlow");

        assertTrue(config.getStateConfig(YamlTestStates.FirstLevelState.class).getActionToStateMapping().containsKey("AddedAction"));
        assertTrue(config.getStateConfig(TestStates.StateAddedThroughFlowExtension.class) != null);
    }

    @Test
    public void testOverrideFlow() {
        YamlConfigProvider provider = new YamlConfigProvider();
        provider.load("pos", "testflows");

        FlowConfig config = provider.getConfigByName("pos", "11111", "TestFlow");
        assertEquals("StateToOverride", config.getStateConfig(YamlTestStates.OverrideState.class).getStateName());
        assertNull(config.getStateConfig(YamlTestStates.StateToOverride.class));
    }
}
