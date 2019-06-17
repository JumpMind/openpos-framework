package org.jumpmind.pos.core.flow.config;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.jumpmind.pos.core.flow.config.YamlTestStates.FirstLevelState;
import org.jumpmind.pos.core.flow.config.YamlTestStates.InitialState;
import org.jumpmind.pos.core.flow.config.YamlTestStates.InitialState2;
import org.jumpmind.pos.core.flow.config.YamlTestStates.InlineState;
import org.jumpmind.pos.core.flow.config.YamlTestStates.InlineState2;
import org.jumpmind.pos.core.flow.config.YamlTestStates.NestedState;
import org.jumpmind.pos.core.flow.config.YamlTestStates.SimpleState;
import org.jumpmind.pos.core.flow.config.YamlTestStates.SubstateClassTestState;
import org.junit.Test;

public class YamlConfigConverterTest {

    @Test
    public void testLoadNestedInlineState() {
        YamlFlowConfigFileLoader loader = new YamlFlowConfigFileLoader();
        List<YamlFlowConfig> yamlFlowConfigs = loader.loadYamlFlowConfigs("testflows/test-nested-flow.yml");
        
        YamlFlowConfig yamlFlowConfig = yamlFlowConfigs.get(0);

        assertEquals("TestNestedFlow", yamlFlowConfig.getFlowName());
        assertEquals(4, yamlFlowConfig.getFlowStateConfigs().size());
        YamlStateConfig initialState = yamlFlowConfig.getFlowStateConfigs().get(0);
        assertEquals("InitialState", initialState.getStateName());
// InlineState
        assertNotNull(initialState.getActionToStateConfigs().get("GotoNestedInlineState"));
        YamlStateConfig inlineState = initialState.getActionToStateConfigs().get("GotoNestedInlineState");
        assertEquals("InlineState", inlineState.getStateName());
        YamlStateConfig nestedState = inlineState.getActionToStateConfigs().get("GotoNestedState");
        assertEquals("NestedState", nestedState.getStateName());
        YamlStateConfig initialStateReference = nestedState.getActionToStateConfigs().get("Back");
        assertEquals("InitialState", initialStateReference.getStateName());
        
        YamlConfigConverter converter = new YamlConfigConverter();
        List<FlowConfig> flowConfig = converter.convertToFlowConfig(yamlFlowConfigs);        
    }
    
    @Test
    
    public void testConvertStateYaml() {
        YamlFlowConfigFileLoader loader = new YamlFlowConfigFileLoader();
        List<YamlFlowConfig> yamlFlowConfigs = loader.loadYamlFlowConfigs("testflows/test-flow.yml");
        YamlConfigConverter converter = new YamlConfigConverter();
        List<FlowConfig> flowConfigs = converter.convertToFlowConfig(yamlFlowConfigs);

        {
            FlowConfig testFlow = flowConfigs.get(0);
            assertEquals("TestFlow", testFlow.getName());
            assertEquals(testFlow.getInitialState().getStateClass(), InitialState.class);
            StateConfig initialState = testFlow.getInitialState(); 
            assertEquals(initialState.getActionToStateMapping().get("SimpleAction"), SimpleState.class);
            assertEquals(initialState.getActionToStateMapping().get("GotoInlineState"), InlineState.class);
            assertEquals(initialState.getActionToStateMapping().get("GotoNestedInlineState"), InlineState2.class);
            {                
                SubTransition substateClassTestStateTransition = initialState.getActionToSubStateMapping().get("GotoSubstateClass"); 
                assertArrayEquals(new String[] {"SubstateClassReturnAction"}, substateClassTestStateTransition.getReturnActionNames());
                FlowConfig subFlowConfig = substateClassTestStateTransition.getSubFlowConfig();
                assertEquals(SubstateClassTestState.class, subFlowConfig.getInitialState().getStateClass());
                assertEquals("value1", subFlowConfig.getConfigScope().get("testKey1"));
                assertEquals("value2", subFlowConfig.getConfigScope().get("testKey2"));
            }
            {                
                SubTransition substateFlowTransition = initialState.getActionToSubStateMapping().get("GotoSubstateFlow"); 
                assertArrayEquals(new String[] {"SubstateFlowReturnAction"}, substateFlowTransition.getReturnActionNames());
                FlowConfig subFlowConfig = substateFlowTransition.getSubFlowConfig();
                assertEquals(InitialState2.class, subFlowConfig.getInitialState().getStateClass());
                assertEquals(SimpleState.class, subFlowConfig.getStateConfig(InitialState2.class).getActionToStateMapping().get("SimpleAction"));
            }
            
            {
                StateConfig inlineState = testFlow.getStateConfigs().get(InlineState.class);
                assertEquals("InlineState", inlineState.getStateName());    
                assertEquals(InitialState.class, inlineState.getActionToStateMapping().get("Back"));
                assertEquals(InitialState.class, inlineState.getActionToStateMapping().get("Next"));
            }
            {
                StateConfig inlineState2 = testFlow.getStateConfigs().get(InlineState2.class);
                assertEquals("InlineState2", inlineState2.getStateName());    
                assertEquals(NestedState.class, inlineState2.getActionToStateMapping().get("GotoNestedState"));
            }
            {
                StateConfig nestedState = testFlow.getStateConfigs().get(NestedState.class);
                assertEquals("NestedState", nestedState.getStateName());    
                assertEquals(InitialState.class, nestedState.getActionToStateMapping().get("Back"));
            }
            {
                StateConfig firstLevelState = testFlow.getStateConfigs().get(FirstLevelState.class);
                assertEquals("FirstLevelState", firstLevelState.getStateName());    
                assertEquals(SimpleState.class, firstLevelState.getActionToStateMapping().get("SimpleAction"));
                assertEquals(InlineState.class, firstLevelState.getActionToStateMapping().get("GotoInlineState"));
                assertEquals(InlineState2.class, firstLevelState.getActionToStateMapping().get("GotoNestedInlineState"));
                {                
                    SubTransition substateClassTestStateTransition = firstLevelState.getActionToSubStateMapping().get("GotoSubstateClass"); 
                    assertArrayEquals(new String[] {"SubstateClassReturnAction", "SomeOtherAction"}, substateClassTestStateTransition.getReturnActionNames());
                    FlowConfig subFlowConfig = substateClassTestStateTransition.getSubFlowConfig();
                    assertEquals(SubstateClassTestState.class, subFlowConfig.getInitialState().getStateClass());
                    assertEquals("value3", subFlowConfig.getConfigScope().get("testKey3"));
                    assertEquals("value4", subFlowConfig.getConfigScope().get("testKey4"));                    
                }
                {                
                    SubTransition substateFlowTransition = firstLevelState.getActionToSubStateMapping().get("GotoSubstateFlow"); 
                    assertArrayEquals(new String[] {"SubstateFlowReturnAction"}, substateFlowTransition.getReturnActionNames());
                    FlowConfig subFlowConfig = substateFlowTransition.getSubFlowConfig();
                    assertEquals(InitialState2.class, subFlowConfig.getInitialState().getStateClass());
                }      
                {
                    assertEquals(InitialState.class, testFlow.getActionToStateMapping().get("BackToMain"));
                    assertEquals("SubstateFlow", testFlow.getActionToSubStateMapping().get("GotoGlobalSubstate").getSubFlowConfig().getName());
                }
            }
        }
    }

}
