package org.jumpmind.pos.core.flow;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;


import org.jumpmind.pos.core.clientconfiguration.LocaleMessageFactory;
import org.jumpmind.pos.core.flow.TestStates.HomeState;
import org.jumpmind.pos.core.flow.TestStates.StateWithBeforeActionMethod;
import org.jumpmind.pos.core.flow.TestStates.StateWithBeforeActionMethodThatThrowsException;
import org.jumpmind.pos.core.flow.TestStates.StateWithMultipleBeforeActionAndFailOnExceptionIsFalse;
import org.jumpmind.pos.core.flow.TestStates.StateWithMultipleBeforeActionAndFailOnExceptionIsTrue;
import org.jumpmind.pos.core.flow.TestStates.StateWithMultipleBeforeActionMethods;

import org.jumpmind.pos.core.flow.config.FlowBuilder;
import org.jumpmind.pos.core.flow.config.FlowConfig;
import org.jumpmind.pos.server.service.IMessageService;
import org.jumpmind.pos.util.model.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BeforeActionStateLifeCycleServiceIT {
    
    @InjectMocks
    StateManager stateManager;

    @InjectMocks
    Injector injector;
    
    @Mock
    private IMessageService messageService;
    
    
    @Before
    public void setUp() throws Exception {
        doNothing().when(messageService).sendMessage(any(String.class), any(String.class), any(Message.class));

        FlowConfig config = new FlowConfig();
        config.setInitialState(FlowBuilder.addState(HomeState.class)
            .withTransition("TestSingleBeforeActionMethod", StateWithBeforeActionMethod.class)
            .withTransition("TestMultipleBeforeActionMethods", StateWithMultipleBeforeActionMethods.class)
            .withTransition("TestMultipleBeforeActionMethodsAndFailOnExceptionIsFalse", StateWithMultipleBeforeActionAndFailOnExceptionIsFalse.class)
            .withTransition("TestMultipleBeforeActionMethodsAndFailOnExceptionIsTrue", StateWithMultipleBeforeActionAndFailOnExceptionIsTrue.class)
            .withTransition("TestSingleBeforeActionMethodThatThrowsException", StateWithBeforeActionMethodThatThrowsException.class)
            .build()
        );
        
        LocaleMessageFactory localeMessageFactory = new LocaleMessageFactory();
        TestUtil.setField(localeMessageFactory, "supportedLocales", new String[] {"en_US"});
        TestUtil.setField(stateManager, "localeMessageFactory", localeMessageFactory);
        
        ActionHandlerImpl actionHandler = new ActionHandlerImpl();
        actionHandler.setBeforeActionService(new BeforeActionStateLifecycleService());
        
        stateManager.setInitialFlowConfig(config);
        TestUtil.setField(stateManager, "actionHandler", actionHandler);
        TestUtil.setField(stateManager, "injector", injector);
        TestUtil.setField(stateManager, "outjector", new Outjector());
        TestUtil.setField(stateManager, "stateLifecyce", new StateLifecycle());
        
    }

    @Test
    public void testSingleBeforeActionMethod() {
        stateManager.init("pos", "100-1");
        stateManager.doAction("TestSingleBeforeActionMethod");
        StateWithBeforeActionMethod testState = (StateWithBeforeActionMethod) stateManager.getCurrentState();
        assertFalse(testState.beforeActionInvoked);
        assertFalse(testState.onAction1Invoked);
        stateManager.doAction("Action1");
        assertTrue(testState.beforeActionInvoked);
        assertTrue(testState.onAction1Invoked);
    }

    @Test(expected=FlowException.class)
    public void testSingleBeforeActionMethodThatThrowsException() {
        stateManager.init("pos", "100-1");
        stateManager.doAction("TestSingleBeforeActionMethodThatThrowsException");
        StateWithBeforeActionMethodThatThrowsException testState = (StateWithBeforeActionMethodThatThrowsException) stateManager.getCurrentState();
        assertFalse(testState.beforeActionInvoked);
        assertFalse(testState.onAction1Invoked);
        stateManager.doAction("Action1");
    }
    
    @Test
    public void testMultipleBeforeActionMethods() {
        stateManager.init("pos", "100-1");
        stateManager.doAction("TestMultipleBeforeActionMethods");
        StateWithMultipleBeforeActionMethods testState = (StateWithMultipleBeforeActionMethods) stateManager.getCurrentState();
        assertFalse(testState.beforeAction_AInvoked);
        assertFalse(testState.beforeAction_BInvoked);
        assertFalse(testState.beforeAction_CInvoked);
        assertFalse(testState.onAction1Invoked);
        stateManager.doAction("Action1");
        
        assertTrue(testState.beforeAction_AInvoked);
        assertTrue(testState.beforeAction_BInvoked);
        assertTrue(testState.beforeAction_CInvoked);
        assertTrue(testState.onAction1Invoked);
    }
    
    @Test
    public void testMultipleBeforeActionMethodsAndFailOnExceptionIsFalse() {
        stateManager.init("pos", "100-1");
        stateManager.doAction("TestMultipleBeforeActionMethodsAndFailOnExceptionIsFalse");
        StateWithMultipleBeforeActionAndFailOnExceptionIsFalse testState = (StateWithMultipleBeforeActionAndFailOnExceptionIsFalse) stateManager.getCurrentState();
        assertFalse(testState.beforeAction_AInvoked);
        assertFalse(testState.beforeAction_BInvoked);
        assertFalse(testState.beforeAction_CInvoked);
        assertFalse(testState.beforeAction_DInvoked);
        assertFalse(testState.onAction1Invoked);
        // StateWithMultipleBeforeActionAndFailOnExceptionIsFalse will check that beforeAction methods are fired in correct order
        stateManager.doAction("Action1");
        
        assertTrue(testState.beforeAction_AInvoked);
        assertTrue(testState.beforeAction_BInvoked);
        assertTrue(testState.beforeAction_CInvoked);
        assertFalse(testState.beforeAction_DInvoked);
        assertTrue(testState.onAction1Invoked);
    }

    @Test
    public void testMultipleBeforeActionMethodsAndFailOnExceptionIsTrue() {
        stateManager.init("pos", "100-1");
        stateManager.doAction("TestMultipleBeforeActionMethodsAndFailOnExceptionIsTrue");
        StateWithMultipleBeforeActionAndFailOnExceptionIsTrue testState = (StateWithMultipleBeforeActionAndFailOnExceptionIsTrue) stateManager.getCurrentState();
        assertFalse(testState.beforeAction_AInvoked);
        assertFalse(testState.beforeAction_BInvoked);
        assertFalse(testState.beforeAction_CInvoked);
        assertFalse(testState.beforeAction_DInvoked);
        assertFalse(testState.onAction1Invoked);

        try {
            stateManager.doAction("Action1");
        } catch (FlowException ex) {
            assertNotNull(ex);
            assertFalse(testState.beforeAction_AInvoked);
            assertFalse(testState.beforeAction_BInvoked);
            // BeforeAction_C is the only method that should have executed
            assertTrue(testState.beforeAction_CInvoked);
            assertFalse(testState.beforeAction_DInvoked);
            assertFalse(testState.onAction1Invoked);
            return;
        }
        fail("A FlowException should have been raised before reaching this point");
    }
    
    
    
}
