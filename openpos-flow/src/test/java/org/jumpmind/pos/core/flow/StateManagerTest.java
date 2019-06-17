package org.jumpmind.pos.core.flow;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.util.Arrays;

import org.jumpmind.pos.core.clientconfiguration.LocaleMessageFactory;
import org.jumpmind.pos.core.flow.TestStates.AboutState;
import org.jumpmind.pos.core.flow.TestStates.ActionTestingState;
import org.jumpmind.pos.core.flow.TestStates.CustomerSearchState;
import org.jumpmind.pos.core.flow.TestStates.CustomerSignupState;
import org.jumpmind.pos.core.flow.TestStates.CustomerState;
import org.jumpmind.pos.core.flow.TestStates.HelpState;
import org.jumpmind.pos.core.flow.TestStates.HomeState;
import org.jumpmind.pos.core.flow.TestStates.InjectionFailedState;
import org.jumpmind.pos.core.flow.TestStates.MultiReturnAction1State;
import org.jumpmind.pos.core.flow.TestStates.MultiReturnAction2State;
import org.jumpmind.pos.core.flow.TestStates.MultiReturnActionInitialState;
import org.jumpmind.pos.core.flow.TestStates.MultiReturnActionTestState;
import org.jumpmind.pos.core.flow.TestStates.OptionalInjectionState;
import org.jumpmind.pos.core.flow.TestStates.RepostActionState;
import org.jumpmind.pos.core.flow.TestStates.SellState;
import org.jumpmind.pos.core.flow.TestStates.StackOverflowState;
import org.jumpmind.pos.core.flow.TestStates.SubStateFlowScopePropogation1;
import org.jumpmind.pos.core.flow.TestStates.SubStateFlowScopePropogation2;
import org.jumpmind.pos.core.flow.TestStates.SubStateReturnsWithTransitionState;
import org.jumpmind.pos.core.flow.TestStates.TestScopesState;
import org.jumpmind.pos.core.flow.TestStates.TransitionInterceptionState;
import org.jumpmind.pos.core.flow.config.FlowBuilder;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.service.ScreenService;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.model.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class StateManagerTest {

    @InjectMocks
    StateManager stateManager;

    @InjectMocks
    Injector injector;
 
    @Mock
    private ScreenService screenService;

    @Mock
    private IMessageService messageService;

    @Before
    public void setup() throws Exception {

        doNothing().when(messageService).sendMessage(any(String.class), any(String.class), any(Message.class));

        FlowConfig customerSignupFlow = new FlowConfig();
        customerSignupFlow.setInitialState(
                FlowBuilder.addState(CustomerSignupState.class).withTransition("CustomerSignedup", CompleteState.class)
                .withTransition("Back", CompleteState.class)
                .build());
        
        FlowConfig testFlowScopeFlow2 = new FlowConfig();
        testFlowScopeFlow2.setInitialState(FlowBuilder.addState(SubStateFlowScopePropogation2.class)
                .withTransition("Back", CompleteState.class)
                .build());
        
        FlowConfig testFlowScopeFlow = new FlowConfig();
        testFlowScopeFlow.setInitialState(FlowBuilder.addState(SubStateFlowScopePropogation1.class)
                .withTransition("Back", CompleteState.class)
                .withSubTransition("SubStateFlowScopePropogation2Action", testFlowScopeFlow2, "SubStateFlowScopePropogation2DONE")
                .build());

        FlowConfig customerFlow = new FlowConfig();
        customerFlow.getConfigScope().put("customerFlowType", "LOYALTY");
        customerFlow.setInitialState(FlowBuilder.addState(CustomerState.class).withTransition("CustomerSearch", CustomerSearchState.class)
                .withTransition("Back", CompleteState.class)
                .withTransition("CustomerSelected", CompleteState.class)
                .withSubTransition("CustomerSignup", customerSignupFlow, "CustomerSignupComplete").build());
        customerFlow.add(FlowBuilder.addState(CustomerSearchState.class).withTransition("CustomerSelected", CompleteState.class).build());
        customerFlow.addGlobalTransitionOrActionHandler("Help", HelpState.class);
        customerFlow.addGlobalSubTransition("CustomerSignup", customerSignupFlow);
        customerFlow.add(FlowBuilder.addState(HelpState.class).withTransition("Back", CustomerState.class).build());
        
        FlowConfig multiReturnActionSubFlow = new FlowConfig();
        multiReturnActionSubFlow.setInitialState(FlowBuilder.addState(MultiReturnActionInitialState.class).build());

        FlowConfig config = new FlowConfig();
        config.setInitialState(FlowBuilder.addState(HomeState.class).withTransition("Sell", SellState.class)
                .withSubTransition("ToSubState1", SubStateReturnsWithTransitionState.class, "FromSubStateToAnotherState")
                .withSubTransition("ToSubState2", SubStateReturnsWithTransitionState.class, "FromSubStateToAnotherSubState")
                .withSubTransition("TestFlowScope", testFlowScopeFlow, "Return")
                .withTransition("FromSubStateToAnotherState", ActionTestingState.class)
                .withTransition("StartMultiReturnActionTest", MultiReturnActionTestState.class)
                .withSubTransition("FromSubStateToAnotherSubState", customerFlow, "Return")
                .withTransition("TestActions", ActionTestingState.class).withTransition("TestScopes", TestScopesState.class)
                .withTransition("TestTransitionInterception", TransitionInterceptionState.class)
                .withTransition("TestFailedInjections", InjectionFailedState.class)
                .withTransition("TestOptionalInjections", OptionalInjectionState.class)
                .withTransition("StackOverflow", StackOverflowState.class).build());
        config.add(FlowBuilder.addState(TestScopesState.class).withTransition("Done", HomeState.class).build());
        config.add(FlowBuilder.addState(SellState.class).withSubTransition("Customer", customerFlow, "CustomerLookupComplete").build());
        config.add(FlowBuilder.addState(ActionTestingState.class).withTransition("Done", HomeState.class).build());
        config.add(FlowBuilder.addState(TransitionInterceptionState.class).withTransition("Sell", SellState.class).build());
        config.add(FlowBuilder.addState(MultiReturnActionTestState.class)
                .withSubTransition("EnterSubstateInMultiReturnActionTest", multiReturnActionSubFlow, "MultiReturnAction1", "MultiReturnAction2")
                .withSubTransition("EnterSubstateInMultiReturnActionTestNoFlowConfig", MultiReturnActionInitialState.class, "MultiReturnAction1", "MultiReturnAction2")
                .withTransition("MultiReturnAction1", MultiReturnAction1State.class)
                .withTransition("MultiReturnAction2", MultiReturnAction2State.class)
                .build());
        


        config.addGlobalTransitionOrActionHandler("Help", HelpState.class);
        config.addGlobalTransitionOrActionHandler("About", AboutState.class);
        config.addGlobalTransitionOrActionHandler("Home", HomeState.class);
        config.addGlobalTransitionOrActionHandler("RepostActionState", RepostActionState.class);
        config.addGlobalTransitionOrActionHandler("RepostActionStateGotToSell", SellState.class);
        config.addGlobalTransitionOrActionHandler("TestTransitionProceed", HomeState.class);
        config.addGlobalTransitionOrActionHandler("TestTransitionCancel", HomeState.class);
        config.addGlobalSubTransition("CustomerLookupGlobal", customerFlow);
        // config.addGlobalSubTransition("VendorList", vendorFlow);
        
        LocaleMessageFactory localeMessageFactory = new LocaleMessageFactory();
        TestUtil.setField(localeMessageFactory, "supportedLocales", new String[] {"en_US"});
        TestUtil.setField(stateManager, "localeMessageFactory", localeMessageFactory);
        
        stateManager.setInitialFlowConfig(config);
        TestUtil.setField(stateManager, "actionHandler", new ActionHandlerImpl());
        TestUtil.setField(stateManager, "injector", injector);
        TestUtil.setField(stateManager, "outjector", new Outjector());
        TestUtil.setField(stateManager, "transitionSteps", Arrays.asList(new TestTransitionStepCancel(), new TestTransitionStepProceed()));
        TestUtil.setField(stateManager, "stateLifecyce", new StateLifecycle());
        

    }

    
    @Test
    public void testSubStateTransitionBackToAnotherState() {
        stateManager.init("pos", "100-1");
        
        HomeState homeState = (HomeState) stateManager.getCurrentState();
        homeState.departGeneralCalled = false;
        homeState.departToSubflowCalled = false;
        homeState.departStateCalled = false;
        
        assertEquals(HomeState.class, homeState.getClass());
        assertTrue(homeState.arriveCalled);
        stateManager.doAction("ToSubState1");
        
        assertTrue(homeState.departGeneralCalled);
        assertTrue(homeState.departToSubflowCalled);
        assertFalse(homeState.departStateCalled);
        
        assertEquals(ActionTestingState.class, stateManager.getCurrentState().getClass());
    }
    
    @Test
    public void testSubStateTransitionBackToAnotherSubState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("ToSubState2");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Back");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }


    @Test
    public void testInitialState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSimpleTransition() {
        stateManager.init("pos", "100-1");
        HomeState homeState = (HomeState) stateManager.getCurrentState();
        homeState.departGeneralCalled = false;
        homeState.departToSubflowCalled = false;
        homeState.departStateCalled = false;
        
        assertEquals(HomeState.class, homeState.getClass());
        stateManager.doAction("Sell");
        assertTrue(homeState.departGeneralCalled);
        assertFalse(homeState.departToSubflowCalled);
        assertTrue(homeState.departStateCalled);
        assertEquals("Sell", homeState.departAction.getName());
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSpecificVsAnyActionHandler() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("TestActions");
        assertEquals(ActionTestingState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("SpecificAction");
        stateManager.doAction("Done");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertTrue("stateManager.getScopeValue(\"specificActionMethodCalled\")", stateManager.getScopeValue("specificActionMethodCalled"));
        assertFalse("stateManager.getScopeValue(\"anyActionMethodCalled\")", stateManager.getScopeValue("anyActionMethodCalled"));

        stateManager.doAction("TestActions");
        assertEquals(ActionTestingState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("ActionHandledByAction");
        stateManager.doAction("Done");
        assertFalse("stateManager.getScopeValue(\"specificActionMethodCalled\")", stateManager.getScopeValue("specificActionMethodCalled"));
        assertTrue("stateManager.getScopeValue(\"anyActionMethodCalled\")", stateManager.getScopeValue("anyActionMethodCalled"));
    }

    @Test
    public void testActionInterception() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("TestTransitionInterception");
        assertEquals(TransitionInterceptionState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertTrue("stateManager.getScopeValue(\"onSellCalled\")", stateManager.getScopeValue("onSellCalled"));
        assertFalse("stateManager.getScopeValue(\"anyActionMethodCalled\")", stateManager.getScopeValue("anyActionMethodCalled"));
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSubState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSubSubState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSignup");
        assertEquals(CustomerSignupState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSignedup");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testFlowScope() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSearch");
        assertEquals(CustomerSearchState.class, stateManager.getCurrentState().getClass());
        assertEquals("customer1234", stateManager.getScopeValue("selectedCustomer"));
        stateManager.doAction("CustomerSelected");
        assertNull("stateManager.getScopeValue(\"selectedCustomer\")", stateManager.getScopeValue("selectedCustomer"));
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }
    
    @Test
    public void testFlowScopePropogation() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertNull("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));
        stateManager.doAction("TestFlowScope");
        assertEquals(SubStateFlowScopePropogation1.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("SubStateFlowScopePropogation2Action");
        assertEquals(SubStateFlowScopePropogation2.class, stateManager.getCurrentState().getClass());
        assertEquals("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));        
        stateManager.doAction("Back");
        assertEquals(SubStateFlowScopePropogation1.class, stateManager.getCurrentState().getClass());
        assertEquals("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));
        stateManager.doAction("Back");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertNull("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));
    }

    @Test
    public void testScopes() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("TestScopes");
        assertEquals(TestScopesState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Done");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());

        assertEquals("conversationScopeValue", stateManager.getScopeValue("conversationScopeValue"));
        assertEquals("sessionScopeValue", stateManager.getScopeValue("sessionScopeValue"));
        assertEquals("nodeScopeValue", stateManager.getScopeValue("nodeScopeValue"));

        stateManager.endConversation();
        assertNull(stateManager.getScopeValue("conversationScopeValue"), stateManager.getScopeValue("conversationScopeValue"));
        assertEquals("sessionScopeValue", stateManager.getScopeValue("sessionScopeValue"));
        assertEquals("nodeScopeValue", stateManager.getScopeValue("nodeScopeValue"));

        stateManager.endSession();
        assertNull(stateManager.getScopeValue("conversationScopeValue"), stateManager.getScopeValue("conversationScopeValue"));
        assertNull("stateManager.getScopeValue(\"sessionScopeValue\")", stateManager.getScopeValue("sessionScopeValue"));
        assertEquals("nodeScopeValue", stateManager.getScopeValue("nodeScopeValue"));
    }

    @Test
    public void testInjectConfigState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        assertEquals("customerFlowTypeWorked", stateManager.getScopeValue("customerFlowTypeWorked"));
    }

    @Test(expected = FlowException.class)
    public void testUnhandledAction() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("UnhandledAction");
    }

    @Test(expected = FlowException.class)
    public void testInjectionFailure() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("TestFailedInjections");
    }

    @Test
    public void testOptionalInjections() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("TestOptionalInjections");
        assertEquals(OptionalInjectionState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalTransitionFromIntialState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Help");
        assertEquals(HelpState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalTransitionFromSubsquentState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Help");
        assertEquals(HelpState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("About");
        assertEquals(AboutState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Home");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalTransitionFromSubState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Help");
        assertEquals(HelpState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Back");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalSubTransitionFromIntialState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerLookupGlobal");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSignup");
        assertEquals(CustomerSignupState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSignedup");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSelected");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalSubTransitionFromSubState() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSignup");
        assertEquals(CustomerSignupState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSignedup");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Home");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testTransitionProceed() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("TestTransitionProceed");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testTransitionCancel() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("TestTransitionCancel");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test(expected = FlowException.class)
    public void testStackOverflow() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("StackOverflow");
    }
    
    @Test
    public void testMultiSubFlowReturnActions1() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("EnterSubstateInMultiReturnActionTest");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("MultiReturnAction1");
        assertEquals(MultiReturnAction1State.class, stateManager.getCurrentState().getClass());
    }
    
    @Test
    public void testMultiSubFlowReturnActions2() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("EnterSubstateInMultiReturnActionTest");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("MultiReturnAction2");
        assertEquals(MultiReturnAction2State.class, stateManager.getCurrentState().getClass());
    }
    
    @Test
    public void testMultiSubFlowReturnActions1_NoFlowConfig() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("EnterSubstateInMultiReturnActionTestNoFlowConfig");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("MultiReturnAction1");
        assertEquals(MultiReturnAction1State.class, stateManager.getCurrentState().getClass());
    }
    
    @Test
    public void testMultiSubFlowReturnActions2_NoFlowConfig() {
        stateManager.init("pos", "100-1");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("EnterSubstateInMultiReturnActionTestNoFlowConfig");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        stateManager.doAction("MultiReturnAction2");
        assertEquals(MultiReturnAction2State.class, stateManager.getCurrentState().getClass());
    }
    


}
