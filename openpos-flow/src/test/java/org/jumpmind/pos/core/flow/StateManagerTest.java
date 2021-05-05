package org.jumpmind.pos.core.flow;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.jumpmind.pos.core.error.IErrorHandler;
import org.jumpmind.pos.core.flow.TestStates.AboutState;
import org.jumpmind.pos.core.flow.TestStates.ActionTestingState;
import org.jumpmind.pos.core.flow.TestStates.CustomerSearchState;
import org.jumpmind.pos.core.flow.TestStates.CustomerSignupState;
import org.jumpmind.pos.core.flow.TestStates.CustomerState;
import org.jumpmind.pos.core.flow.TestStates.ExceptionInActionHandlerState;
import org.jumpmind.pos.core.flow.TestStates.ExceptionOnArriveState;
import org.jumpmind.pos.core.flow.TestStates.ExceptionOnDepartState;
import org.jumpmind.pos.core.flow.TestStates.GlobalActionHandler;
import org.jumpmind.pos.core.flow.TestStates.GlobalActionHandlerWithException;
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
import org.jumpmind.pos.core.flow.TestStates.StateWithHandlerForTerminatingAction;
import org.jumpmind.pos.core.flow.TestStates.SetVariablesState;
import org.jumpmind.pos.core.flow.TestStates.UnsetVariablesState;
import org.jumpmind.pos.core.flow.TestStates.CheckVariablesState;
import org.jumpmind.pos.core.flow.config.FlowBuilder;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.core.flow.config.TransitionStepConfig;
import org.jumpmind.pos.core.service.ScreenService;
import org.jumpmind.pos.server.model.Action;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.model.Message;
import org.jumpmind.pos.util.startup.DeviceStartupTaskConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
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

    @Mock
    private IErrorHandler errorHandler;

    @Mock
    private ActionHandlerHelper actionHelper;

    @Mock
    private DeviceStartupTaskConfig deviceStartupTaskConfig;

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

        FlowConfig terminatingActionHandlerSubFlow = new FlowConfig();
        terminatingActionHandlerSubFlow.setInitialState(FlowBuilder.addState(StateWithHandlerForTerminatingAction.class).withTransition("TerminatingAction", CompleteState.class).build());

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
                .withTransition("StackOverflow", StackOverflowState.class)
                .withTransition("TestExceptionOnArriveAction", ExceptionOnArriveState.class)
                .withTransition("TestExceptionOnDepartAction", ExceptionOnDepartState.class)
                .withTransition("GoToExceptionInActionHandlerState", ExceptionInActionHandlerState.class)
                .withSubTransition( "ToSubFlowWithActionActionHandlerForTerminatingState", terminatingActionHandlerSubFlow)
                .withTransition("SetVariables", SetVariablesState.class)
                .withTransition("UnsetVariables", UnsetVariablesState.class)
                .withTransition("CheckVariables", CheckVariablesState.class)
                .withTransition("CheckOverrideState", TestStates.OverrideSimpleState.class)
                .build()
        );
        config.add(FlowBuilder.addState(SetVariablesState.class).withTransition("UnsetVariables", UnsetVariablesState.class).build());
        config.add(FlowBuilder.addState(UnsetVariablesState.class).withTransition("CheckVariables", CheckVariablesState.class).build());
        config.add(FlowBuilder.addState(CheckVariablesState.class).build());
        config.add(FlowBuilder.addState(ExceptionInActionHandlerState.class).withTransition("ThrowsExceptionAction", HomeState.class).build());
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
        config.addGlobalTransitionOrActionHandler("TestTransitionCancelWithQueuedAction", SellState.class);
        config.addGlobalTransitionOrActionHandler("SomeGlobalAction", GlobalActionHandler.class);
        config.addGlobalTransitionOrActionHandler("SomeGlobalActionWithException", GlobalActionHandlerWithException.class);
        config.addGlobalSubTransition("CustomerLookupGlobal", customerFlow);

        stateManager = StateManagerTestUtils.buildStateManager(injector, null);

        stateManager.setInitialFlowConfig(config);

        TestUtil.setField(stateManager, "transitionStepConfigs", buildTestTransitionSteps());

        stateManager.setErrorHandler(null);
    }

    private List<TransitionStepConfig> buildTestTransitionSteps() {
        List<TransitionStepConfig> transitionSteps = new ArrayList<>();
        {
            TransitionStepConfig config = new TransitionStepConfig();
            config.setTransitionStepClass(TestTransitionStepCancel.class);
            transitionSteps.add(config);
        }
        {
            TransitionStepConfig config = new TransitionStepConfig();
            config.setTransitionStepClass(TestTransitionStepProceed.class);
            transitionSteps.add(config);
        }
        {
            TransitionStepConfig config = new TransitionStepConfig();
            config.setTransitionStepClass(TestTransitionStepCancelWithQueuedAction.class);
            transitionSteps.add(config);
        }

        return transitionSteps;
    }

    @Test
    public void testSubStateTransitionBackToAnotherState() {
        stateManagerInit();

        HomeState homeState = (HomeState) stateManager.getCurrentState();
        homeState.departGeneralCalled = false;
        homeState.departToSubflowCalled = false;
        homeState.departStateCalled = false;

        assertEquals(HomeState.class, homeState.getClass());
        assertTrue(homeState.arriveCalled);
        doAction("ToSubState1");

        assertTrue(homeState.departGeneralCalled);
        assertTrue(homeState.departToSubflowCalled);
        assertFalse(homeState.departStateCalled);

        assertEquals(ActionTestingState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSubStateTransitionBackToAnotherSubState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("ToSubState2");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("Back");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }


    @Test
    public void testInitialState() throws Exception {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    private void stateManagerInit() {
        stateManager.init("pos", "100-1");
        try {
            Thread.sleep(100);
            while (stateManager.getCurrentState() == null) {
                Thread.sleep(10);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private void doAction(Action action) {
        StateManagerTestUtils.doAction(stateManager, action);
    }


    private void doAction(String actionName) {
        StateManagerTestUtils.doAction(stateManager, new Action(actionName));
    }

    @Test
    public void testSimpleTransition() {
        stateManagerInit();
        HomeState homeState = (HomeState) stateManager.getCurrentState();
        homeState.departGeneralCalled = false;
        homeState.departToSubflowCalled = false;
        homeState.departStateCalled = false;

        assertEquals(HomeState.class, homeState.getClass());
        doAction("Sell");
        assertTrue(homeState.departGeneralCalled);
        assertFalse(homeState.departToSubflowCalled);
        assertTrue(homeState.departStateCalled);
        assertEquals("Sell", homeState.departAction.getName());
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSpecificVsAnyActionHandler() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("TestActions");
        assertEquals(ActionTestingState.class, stateManager.getCurrentState().getClass());
        doAction("SpecificAction");
        doAction("Done");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertTrue("stateManager.getScopeValue(\"specificActionMethodCalled\")", stateManager.getScopeValue("specificActionMethodCalled"));
        assertFalse("stateManager.getScopeValue(\"anyActionMethodCalled\")", stateManager.getScopeValue("anyActionMethodCalled"));

        doAction("TestActions");
        assertEquals(ActionTestingState.class, stateManager.getCurrentState().getClass());
        doAction("ActionHandledByAction");
        doAction("Done");
        assertFalse("stateManager.getScopeValue(\"specificActionMethodCalled\")", stateManager.getScopeValue("specificActionMethodCalled"));
        assertTrue("stateManager.getScopeValue(\"anyActionMethodCalled\")", stateManager.getScopeValue("anyActionMethodCalled"));
    }

    @Test
    public void testActionInterception() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("TestTransitionInterception");
        assertEquals(TransitionInterceptionState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");

        assertNotNull("stateManager.getScopeValue(\"onSellCalled\")", stateManager.getScopeValue("onSellCalled"));
        assertFalse("stateManager.getScopeValue(\"anyActionMethodCalled\")", stateManager.getScopeValue("anyActionMethodCalled"));
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSubState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testSubSubState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSignup");
        assertEquals(CustomerSignupState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSignedup");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testFlowScope() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSearch");
        assertEquals(CustomerSearchState.class, stateManager.getCurrentState().getClass());
        assertEquals("customer1234", stateManager.getScopeValue("selectedCustomer"));
        doAction("CustomerSelected");
        assertNull("stateManager.getScopeValue(\"selectedCustomer\")", stateManager.getScopeValue("selectedCustomer"));
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testFlowScopePropogation() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertNull("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));
        doAction("TestFlowScope");
        assertEquals(SubStateFlowScopePropogation1.class, stateManager.getCurrentState().getClass());
        doAction("SubStateFlowScopePropogation2Action");
        assertEquals(SubStateFlowScopePropogation2.class, stateManager.getCurrentState().getClass());
        assertEquals("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));
        doAction("Back");
        assertEquals(SubStateFlowScopePropogation1.class, stateManager.getCurrentState().getClass());
        assertEquals("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));
        doAction("Back");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertNull("flowScopeValue1", stateManager.getScopeValue("flowScopeValue1"));
    }

    @Test
    public void testScopes() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("TestScopes");
        assertEquals(TestScopesState.class, stateManager.getCurrentState().getClass());
        doAction("Done");
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
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        assertEquals("customerFlowTypeWorked", stateManager.getScopeValue("customerFlowTypeWorked"));
    }

    @Test
    public void testUnhandledAction() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());

        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();

        final AtomicBoolean errorHandlerCalled = new AtomicBoolean(false);

        try {
            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
                    assertEquals(FlowException.class, ex.getClass());
                    assertTrue(ex.getMessage().contains("UnhandledAction"));
                    errorHandlerCalled.set(true);
                }
            });

            doAction("UnhandledAction");
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }
    }

    @Test
    public void testUnhandledAction_WithErrorHandler() {
        ArgumentCaptor<Throwable> exArgument = ArgumentCaptor.forClass(Throwable.class);
        stateManager.failOnUnmatchedAction = true;
        stateManager.setErrorHandler(errorHandler);
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("UnhandledAction");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        verify(errorHandler).handleError(eq(stateManager), exArgument.capture());
        assertEquals(FlowException.class, exArgument.getValue().getClass());
        assertTrue(exArgument.getValue().getMessage().contains("UnhandledAction"));

    }


    @Test
    public void testInjectionFailure() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());

        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();
        final AtomicBoolean errorHandlerCalled = new AtomicBoolean(false);

        try {
            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    // TODO: we transitioned to InjectionFailedState, but injections failed.  Expected behavior?
                    // assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
                    assertEquals(FlowException.class, ex.getClass());
                    assertTrue(ex.getMessage().contains("inject"));
                    errorHandlerCalled.set(true);
                }
            });
            doAction("TestFailedInjections");
            assertTrue("should have thrown exception for failed injections", errorHandlerCalled.get());
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }
    }

    @Test
    public void testInjectionFailure_WithErrorHandler() {
        ArgumentCaptor<Throwable> exArgument = ArgumentCaptor.forClass(Throwable.class);

        stateManager.setErrorHandler(errorHandler);
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("TestFailedInjections");
        verify(errorHandler).handleError(eq(stateManager), exArgument.capture());
        assertEquals(FlowException.class, exArgument.getValue().getClass());
        assertTrue(exArgument.getValue().getMessage().contains("inject"));
    }

    @Test
    public void testOptionalInjections() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("TestOptionalInjections");
        assertEquals(OptionalInjectionState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalTransitionFromIntialState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Help");
        assertEquals(HelpState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalTransitionFromSubsquentState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Help");
        assertEquals(HelpState.class, stateManager.getCurrentState().getClass());
        doAction("About");
        assertEquals(AboutState.class, stateManager.getCurrentState().getClass());
        doAction("Home");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalTransitionFromSubState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("Help");
        assertEquals(HelpState.class, stateManager.getCurrentState().getClass());
        doAction("Back");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalSubTransitionFromIntialState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerLookupGlobal");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSignup");
        assertEquals(CustomerSignupState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSignedup");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSelected");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalSubTransitionFromSubState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("Customer");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSignup");
        assertEquals(CustomerSignupState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSignedup");
        assertEquals(CustomerState.class, stateManager.getCurrentState().getClass());
        doAction("CustomerSelected");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("Home");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testGlobalActionHandler() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        class Invoked { boolean invoked = false; }
        final Invoked i = new Invoked();
        Runnable r = () -> i.invoked = true;

        assertFalse(i.invoked);
        doAction(new Action("SomeGlobalAction", r));
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertTrue(i.invoked);
    }

    @Test
    public void testGlobalActionHandlerException() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());

        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();
        final AtomicBoolean errorHandlerCalled = new AtomicBoolean(false);

        try {
            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
                    assertEquals(FlowException.class, ex.getClass());
                    assertEquals(InvocationTargetException.class, ex.getCause().getClass());
                    assertEquals(NullPointerException.class, ex.getCause().getCause().getClass());
                    assertTrue( ex.getCause().getCause().getMessage().contains("Global"));
                    errorHandlerCalled.set(true);
                }
            });
            doAction("SomeGlobalActionWithException");
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }
    }

    @Test
    public void testGlobalActionHandlerException_WithErrorHandler() {
        ArgumentCaptor<Throwable> exArgument = ArgumentCaptor.forClass(Throwable.class);

        stateManager.setErrorHandler(errorHandler);
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction(new Action("SomeGlobalActionWithException"));
        verify(errorHandler).handleError(eq(stateManager), exArgument.capture());
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        assertEquals(FlowException.class, exArgument.getValue().getClass());
        assertEquals(InvocationTargetException.class, exArgument.getValue().getCause().getClass());
        assertEquals(NullPointerException.class, exArgument.getValue().getCause().getCause().getClass());
        assertTrue( exArgument.getValue().getCause().getCause().getMessage().contains("Global"));
    }

    @Test
    public void testTransitionProceed() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("TestTransitionProceed");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testTransitionCancel() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("TestTransitionCancel");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testTransitionCancelWithQueueAction() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("Sell");
        assertEquals(SellState.class, stateManager.getCurrentState().getClass());
        doAction("TestTransitionCancelWithQueuedAction");
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
    }

    // TODO: temp ignore this to see if it is the culprit for the hanging build
    @Ignore
    @Test(expected = FlowException.class)
    public void testStackOverflow() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("StackOverflow");
    }

    // TODO: temp ignore this to see if it is the culprit for the hanging build
    @Ignore
    @Test
    public void testStackOverflow_WithErrorHandler() {
        stateManager.setErrorHandler(errorHandler);
        this.testStackOverflow();
        verify(errorHandler, atLeastOnce()).handleError(eq(stateManager), isA(FlowException.class));
    }

    @Test
    public void testMultiSubFlowReturnActions1() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        doAction("EnterSubstateInMultiReturnActionTest");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        doAction("MultiReturnAction1");
        assertEquals(MultiReturnAction1State.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testMultiSubFlowReturnActions2() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        doAction("EnterSubstateInMultiReturnActionTest");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        doAction("MultiReturnAction2");
        assertEquals(MultiReturnAction2State.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testMultiSubFlowReturnActions1_NoFlowConfig() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        doAction("EnterSubstateInMultiReturnActionTestNoFlowConfig");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        doAction("MultiReturnAction1");
        assertEquals(MultiReturnAction1State.class, stateManager.getCurrentState().getClass());
    }

    @Test
    public void testMultiSubFlowReturnActions2_NoFlowConfig() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("StartMultiReturnActionTest");
        assertEquals(MultiReturnActionTestState.class, stateManager.getCurrentState().getClass());
        doAction("EnterSubstateInMultiReturnActionTestNoFlowConfig");
        assertEquals(MultiReturnActionInitialState.class, stateManager.getCurrentState().getClass());
        doAction("MultiReturnAction2");
        assertEquals(MultiReturnAction2State.class, stateManager.getCurrentState().getClass());
    }


    @Test
    public void testOnArriveThatThrowsException( ) {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());

        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();
        final AtomicBoolean errorHandlerCalled = new AtomicBoolean(false);

        try {
            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    assertEquals(ExceptionOnArriveState.class, stateManager.getCurrentState().getClass());
                    assertEquals(FlowException.class, ex.getClass());
                    assertEquals(InvocationTargetException.class, ex.getCause().getClass());
                    assertEquals(NullPointerException.class, ex.getCause().getCause().getClass());
                    assertTrue( ex.getCause().getCause().getMessage().contains("arrive"));
                    errorHandlerCalled.set(true);
                }
            });
            doAction("TestExceptionOnArriveAction");
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }
    }

    @Test
    public void testOnArriveThatThrowsException_WithErrorHandler( ) {
        ArgumentCaptor<Throwable> exArgument = ArgumentCaptor.forClass(Throwable.class);

        stateManager.setErrorHandler(errorHandler);
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("TestExceptionOnArriveAction");

        verify(errorHandler).handleError(eq(stateManager), exArgument.capture());

        assertEquals(ExceptionOnArriveState.class, stateManager.getCurrentState().getClass());
        assertEquals(FlowException.class, exArgument.getValue().getClass());
        assertEquals(InvocationTargetException.class, exArgument.getValue().getCause().getClass());
        assertEquals(NullPointerException.class, exArgument.getValue().getCause().getCause().getClass());
        assertTrue(exArgument.getValue().getCause().getCause().getMessage().contains("arrive"));
    }

    @Test
    public void testOnDepartThatThrowsException( ) {
        stateManagerInit();
        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();
        final AtomicBoolean errorHandlerCalled = new AtomicBoolean(false);
        try {

            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    assertEquals(FlowException.class, ex.getClass());
                    assertEquals(NullPointerException.class, ex.getCause().getCause().getClass());
                    assertTrue(ex.getCause().getCause().getMessage().contains("departure"));
                    errorHandlerCalled.set(true);
                }
            });
            assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
            doAction("TestExceptionOnDepartAction");
            assertEquals(ExceptionOnDepartState.class, stateManager.getCurrentState().getClass());

            doAction("Home");
            assertTrue("Should have failed in onDepart", errorHandlerCalled.get());
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }
    }

    @Test
    public void testOnDepartThatThrowsException_WithErrorHandler( ) {
        ArgumentCaptor<Throwable> exArgument = ArgumentCaptor.forClass(Throwable.class);

        stateManager.setErrorHandler(errorHandler);

        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("TestExceptionOnDepartAction");
        assertEquals(ExceptionOnDepartState.class, stateManager.getCurrentState().getClass());
        doAction("Home");
        verify(errorHandler).handleError(eq(stateManager), exArgument.capture());

        assertEquals(FlowException.class, exArgument.getValue().getClass());
        assertEquals(NullPointerException.class, exArgument.getValue().getCause().getCause().getClass());
        assertTrue(exArgument.getValue().getCause().getCause().getMessage().contains("departure"));
    }

    /**
     *  Ensure that a flow variable which is cleared in the onDepart method of a state, is still cleared upon arrival
     *  at a subsequent state.
     */
    @Test
    public void testFlowVariableClearOnDepart() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("SetVariables");
        assertEquals(SetVariablesState.class, stateManager.getCurrentState().getClass());
        assertEquals("value-1", ((SetVariablesState) stateManager.getCurrentState()).variableToClear);
        doAction("UnsetVariables");
        assertEquals(UnsetVariablesState.class, stateManager.getCurrentState().getClass());
        assertEquals("value-1", ((UnsetVariablesState) stateManager.getCurrentState()).variableToClear);
        assertEquals("keep-me", ((UnsetVariablesState) stateManager.getCurrentState()).variableToKeep);
        doAction("CheckVariables");
        assertEquals(CheckVariablesState.class, stateManager.getCurrentState().getClass());
        assertNull("variableToClear should be null after OnDepart of UnsetVariablesState", ((CheckVariablesState) stateManager.getCurrentState()).variableToClear);
        assertEquals("keep-me", ((CheckVariablesState) stateManager.getCurrentState()).variableToKeep);
    }

    @Test
    public void testExceptionInActionHandler( ) {
        stateManagerInit();

        IErrorHandler existingErrorHandler = stateManager.getErrorHandler();
        final AtomicBoolean errorHandlerCalled = new AtomicBoolean(false);

        try {
            stateManager.setErrorHandler(new IErrorHandler() {
                @Override
                public void handleError(IStateManager stateManager, Throwable ex) {
                    assertEquals(FlowException.class, ex.getClass());
                    assertEquals(RuntimeException.class, ex.getCause().getCause().getClass());
                    assertTrue(ex.getCause().getCause().getMessage().contains("error in action handler"));
                    errorHandlerCalled.set(true);
                }
            });
            assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
            doAction("GoToExceptionInActionHandlerState");
            assertEquals(ExceptionInActionHandlerState.class, stateManager.getCurrentState().getClass());
            doAction("ThrowsExceptionAction");
            assertTrue("errorHandlerCalled.get()", errorHandlerCalled.get());
        } finally {
            stateManager.setErrorHandler(existingErrorHandler);
        }

    }

    @Test
    public void testExceptionInActionHandler_WithErrorHandler( ) {
        ArgumentCaptor<Throwable> exArgument = ArgumentCaptor.forClass(Throwable.class);

        stateManager.setErrorHandler(errorHandler);
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("GoToExceptionInActionHandlerState");
        assertEquals(ExceptionInActionHandlerState.class, stateManager.getCurrentState().getClass());

        doAction("ThrowsExceptionAction");

        verify(errorHandler).handleError(eq(stateManager), exArgument.capture());
        assertEquals(FlowException.class, exArgument.getValue().getClass());
        assertEquals(InvocationTargetException.class, exArgument.getValue().getCause().getClass());
        assertEquals(RuntimeException.class, exArgument.getValue().getCause().getCause().getClass());
        assertTrue(exArgument.getValue().getCause().getCause().getMessage().contains("error in action handler"));
    }

    @Test
    public void testSubflowWithHandlerForTerminatingAction() {
        stateManagerInit();
        doAction("ToSubFlowWithActionActionHandlerForTerminatingState");
        doAction("TerminatingAction");
        assertTrue(stateManager.getScopeValue("actionHandlerCalled"));
    }

    @Test
    public void testOverrideStateThatExtendsBaseState() {
        stateManagerInit();
        assertEquals(HomeState.class, stateManager.getCurrentState().getClass());
        doAction("CheckOverrideState");
        TestStates.OverrideSimpleState overrideState = (TestStates.OverrideSimpleState) stateManager.getCurrentState();
        assertEquals(TestStates.OverrideSimpleState.class, overrideState.getClass());
        doAction("Something");
        assertEquals(overrideState.message, "Override state message");
    }
}
